package com.nookx.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.nookx.api.domain.MegaAttribute;
import com.nookx.api.domain.MegaAttributeOption;
import com.nookx.api.domain.MegaSetType;
import com.nookx.api.domain.enumeration.AttributeType;
import com.nookx.api.repository.MegaSetTypeRepository;
import com.nookx.api.service.dto.MegaAttributeDTO;
import com.nookx.api.service.dto.MegaSetTypeDTO;
import com.nookx.api.service.mapper.MegaAttributeMapper;
import com.nookx.api.service.mapper.MegaSetTypeMapper;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link MegaSetType} as a versioned, dynamic schema.
 *
 * <h2>Rules</h2>
 * <ul>
 *   <li>An existing {@link MegaSetType} version is <b>never</b> mutated. To evolve a schema
 *       call {@link #createNewVersion(Long, List)}; this creates a new {@link MegaSetType}
 *       with {@code version + 1} and flips {@code isLatest} on the previous one.</li>
 *   <li>Existing {@link com.nookx.api.domain.MegaSet} rows keep pointing at the version they
 *       were created against, so old data continues to work without any schema migration.</li>
 *   <li>Latest schema is exposed via {@code GET /api/mega-set-types?isLatest=true} so the UI
 *       always renders forms against the freshest version.</li>
 * </ul>
 */
@Service
@Transactional
public class MegaSetTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(MegaSetTypeService.class);

    private static final String ENTITY_NAME = "megaSetType";
    private static final String CACHE_NAME = "megaSetTypes";

    private final MegaSetTypeRepository megaSetTypeRepository;
    private final MegaSetTypeMapper megaSetTypeMapper;
    private final MegaAttributeService megaAttributeService;
    private final MegaAttributeMapper megaAttributeMapper;

    public MegaSetTypeService(
        MegaSetTypeRepository megaSetTypeRepository,
        MegaSetTypeMapper megaSetTypeMapper,
        MegaAttributeService megaAttributeService,
        MegaAttributeMapper megaAttributeMapper
    ) {
        this.megaSetTypeRepository = megaSetTypeRepository;
        this.megaSetTypeMapper = megaSetTypeMapper;
        this.megaAttributeService = megaAttributeService;
        this.megaAttributeMapper = megaAttributeMapper;
    }

    // ---------------------------------------------------------------------
    //  Creation / Versioning
    // ---------------------------------------------------------------------

    /**
     * Create a brand new {@link MegaSetType} (version 1).
     * The supplied attribute DTOs are persisted as fresh attributes attached to the new type.
     * Fails if a type with the same name already exists – use {@link #createNewVersion} instead.
     */
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public MegaSetTypeDTO create(MegaSetTypeDTO dto) {
        LOG.debug("Request to create MegaSetType : {}", dto);
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new BadRequestAlertException("name is required", ENTITY_NAME, "namerequired");
        }
        if (megaSetTypeRepository.existsByName(dto.getName())) {
            throw new BadRequestAlertException(
                "A MegaSetType with this name already exists – create a new version instead",
                ENTITY_NAME,
                "nameexists"
            );
        }

        MegaSetType type = new MegaSetType().name(dto.getName()).version(1).active(Boolean.TRUE).isLatest(Boolean.TRUE);

        Set<MegaAttribute> attrs = new HashSet<>();
        if (dto.getAttributes() != null) {
            for (MegaAttributeDTO a : dto.getAttributes()) {
                attrs.add(megaAttributeService.fromDto(a));
            }
        }
        type.setAttributes(attrs);
        type = megaSetTypeRepository.save(type);

        return toDto(type);
    }

    private MegaSetTypeDTO toDto(MegaSetType type) {
        MegaSetTypeDTO result = megaSetTypeMapper.toDto(type);

        Set<MegaAttributeDTO> attributeDTOS = type.getAttributes().stream().map(megaAttributeMapper::toDto).collect(Collectors.toSet());
        result.setAttributes(attributeDTOS);
        return result;
    }

    /**
     * Create a new version of an existing {@link MegaSetType}.
     *
     * <p>Steps:</p>
     * <ol>
     *   <li>Look up the source type (by id) and flip its {@code isLatest} to {@code false}.</li>
     *   <li>Clone all of its attributes (without ids).</li>
     *   <li>Merge with the supplied {@code newAttributes} list, matching by attribute name:
     *       new entries get added, matching entries are patched in place, missing entries are removed.</li>
     *   <li>Persist a new {@link MegaSetType} with {@code version + 1}, {@code isLatest=true}.</li>
     * </ol>
     */
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public MegaSetTypeDTO createNewVersion(Long sourceId, List<MegaAttributeDTO> newAttributes) {
        LOG.debug("Request to create new version of MegaSetType : {}", sourceId);

        MegaSetType source = megaSetTypeRepository
            .findOneWithEagerRelationships(sourceId)
            .orElseThrow(() -> new BadRequestAlertException("Source MegaSetType not found", ENTITY_NAME, "idnotfound"));

        if (Boolean.FALSE.equals(source.getIsLatest())) {
            // we only allow versioning from the current latest to keep history linear
            throw new BadRequestAlertException(
                "Cannot version from a non-latest MegaSetType – fetch the latest version first",
                ENTITY_NAME,
                "notlatest"
            );
        }

        source.setIsLatest(false);
        megaSetTypeRepository.save(source);

        Set<MegaAttribute> cloned = cloneAttributes(source.getAttributes());
        Set<MegaAttribute> merged = mergeAttributes(cloned, newAttributes);

        MegaSetType newVersion = new MegaSetType()
            .name(source.getName())
            .version(source.getVersion() + 1)
            .active(Boolean.TRUE)
            .isLatest(Boolean.TRUE)
            .attributes(merged);

        newVersion = megaSetTypeRepository.save(newVersion);
        return toDto(newVersion);
    }

    private Set<MegaAttribute> cloneAttributes(Set<MegaAttribute> attrs) {
        Set<MegaAttribute> clones = new HashSet<>();
        if (attrs == null) {
            return clones;
        }
        for (MegaAttribute a : attrs) {
            clones.add(megaAttributeService.cloneAttribute(a));
        }
        return clones;
    }

    /**
     * Merge strategy (match by attribute name):
     * <ul>
     *   <li>name in incoming + existing → patch existing</li>
     *   <li>name in incoming, not in existing → add new</li>
     *   <li>name in existing, not in incoming → drop</li>
     * </ul>
     */
    private Set<MegaAttribute> mergeAttributes(Set<MegaAttribute> existing, List<MegaAttributeDTO> incoming) {
        if (incoming == null) {
            return existing;
        }
        Map<String, MegaAttribute> byName = new HashMap<>();
        for (MegaAttribute a : existing) {
            byName.put(a.getName(), a);
        }

        Set<String> incomingNames = new HashSet<>();
        Set<MegaAttribute> result = new HashSet<>();
        for (MegaAttributeDTO dto : incoming) {
            if (dto.getName() == null || dto.getName().isBlank()) {
                throw new BadRequestAlertException("attribute.name is required", ENTITY_NAME, "attrnamerequired");
            }
            incomingNames.add(dto.getName());
            MegaAttribute match = byName.get(dto.getName());
            if (match != null) {
                megaAttributeService.applyPatch(match, dto);
                result.add(match);
            } else {
                result.add(megaAttributeService.fromDto(dto));
            }
        }

        // existing attrs that were not in the incoming list are simply not carried over to
        // the new version – they remain attached to the old version (history preserved).
        return result;
    }

    // ---------------------------------------------------------------------
    //  Reads
    // ---------------------------------------------------------------------

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_NAME, key = "'id:' + #id")
    public Optional<MegaSetTypeDTO> findOne(Long id) {
        LOG.debug("Request to get MegaSetType : {}", id);
        return megaSetTypeRepository.findOneWithEagerRelationships(id).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<MegaSetTypeDTO> findAll() {
        LOG.debug("Request to get all MegaSetTypes");
        return megaSetTypeRepository.findAll().stream().map(this::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Dynamic search for the UI-driven listing endpoint.
     * All filters are optional and combined with AND semantics.
     */
    @Transactional(readOnly = true)
    public List<MegaSetTypeDTO> search(String name, Boolean isLatest, Boolean active) {
        LOG.debug("Request to search MegaSetTypes name={}, isLatest={}, active={}", name, isLatest, active);
        return megaSetTypeRepository.search(name, isLatest, active).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_NAME, key = "'latest:' + #name")
    public Optional<MegaSetTypeDTO> findLatestByName(String name) {
        LOG.debug("Request to get latest MegaSetType by name : {}", name);
        return megaSetTypeRepository.findLatestByName(name).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<MegaSetTypeDTO> findVersionsByName(String name) {
        LOG.debug("Request to get all versions of MegaSetType : {}", name);
        return megaSetTypeRepository.findByNameOrderByVersionDesc(name).stream().map(this::toDto).toList();
    }

    // ---------------------------------------------------------------------
    //  Validation
    // ---------------------------------------------------------------------

    /**
     * Validate a JSON attributes payload (as stored on a MegaSet) against the supplied schema version.
     * Throws {@link BadRequestAlertException} on the first violation, with a stable error key.
     */
    public void validateAttributes(Long typeId, JsonNode attributes) {
        MegaSetType type = megaSetTypeRepository
            .findOneWithEagerRelationships(typeId)
            .orElseThrow(() -> new BadRequestAlertException("MegaSetType not found", ENTITY_NAME, "idnotfound"));
        validateAttributes(type, attributes);
    }

    public void validateAttributes(MegaSetType type, JsonNode attributes) {
        if (type.getAttributes() == null) {
            return;
        }
        for (MegaAttribute attr : type.getAttributes()) {
            JsonNode value = attributes == null ? null : attributes.get(attr.getName());
            boolean isMissing = value == null || value.isNull();

            if (Boolean.TRUE.equals(attr.getRequired()) && isMissing) {
                throw new BadRequestAlertException("Attribute '" + attr.getName() + "' is required", ENTITY_NAME, "attrrequired");
            }
            if (isMissing) {
                continue;
            }

            switch (attr.getType()) {
                case STRING -> validateString(attr, value);
                case NUMBER -> validateNumber(attr, value);
                case BOOLEAN -> validateBoolean(attr, value);
                case DATE -> validateDate(attr, value);
                case ENUM -> validateEnum(attr, value);
            }
        }
    }

    private void validateString(MegaAttribute attr, JsonNode value) {
        if (!value.isTextual()) {
            throw new BadRequestAlertException("Attribute '" + attr.getName() + "' must be a string", ENTITY_NAME, "attrtype");
        }
        String s = value.asText();
        if (attr.getMinLength() != null && s.length() < attr.getMinLength()) {
            throw new BadRequestAlertException("Attribute '" + attr.getName() + "' is too short", ENTITY_NAME, "attrminlength");
        }
        if (attr.getMaxLength() != null && s.length() > attr.getMaxLength()) {
            throw new BadRequestAlertException("Attribute '" + attr.getName() + "' is too long", ENTITY_NAME, "attrmaxlength");
        }
        if (attr.getRegex() != null && !attr.getRegex().isBlank() && !Pattern.matches(attr.getRegex(), s)) {
            throw new BadRequestAlertException("Attribute '" + attr.getName() + "' does not match pattern", ENTITY_NAME, "attrregex");
        }
    }

    private void validateNumber(MegaAttribute attr, JsonNode value) {
        if (!value.isNumber()) {
            throw new BadRequestAlertException("Attribute '" + attr.getName() + "' must be a number", ENTITY_NAME, "attrtype");
        }
        double n = value.asDouble();
        if (attr.getMinNumber() != null && n < attr.getMinNumber()) {
            throw new BadRequestAlertException("Attribute '" + attr.getName() + "' is below the minimum", ENTITY_NAME, "attrminnumber");
        }
        if (attr.getMaxNumber() != null && n > attr.getMaxNumber()) {
            throw new BadRequestAlertException("Attribute '" + attr.getName() + "' is above the maximum", ENTITY_NAME, "attrmaxnumber");
        }
    }

    private void validateBoolean(MegaAttribute attr, JsonNode value) {
        if (!value.isBoolean()) {
            throw new BadRequestAlertException("Attribute '" + attr.getName() + "' must be a boolean", ENTITY_NAME, "attrtype");
        }
    }

    private void validateDate(MegaAttribute attr, JsonNode value) {
        if (!value.isTextual()) {
            throw new BadRequestAlertException("Attribute '" + attr.getName() + "' must be an ISO date string", ENTITY_NAME, "attrtype");
        }
        try {
            LocalDate.parse(value.asText());
        } catch (DateTimeParseException ex) {
            throw new BadRequestAlertException("Attribute '" + attr.getName() + "' is not a valid date", ENTITY_NAME, "attrdate");
        }
    }

    private void validateEnum(MegaAttribute attr, JsonNode value) {
        List<MegaAttributeOption> options = megaAttributeService.findOptions(attr.getId());
        Set<String> allowed = options.stream().map(MegaAttributeOption::getValue).collect(Collectors.toSet());

        List<String> incoming = new ArrayList<>();
        if (value.isArray()) {
            value.forEach(n -> incoming.add(n.asText()));
            if (!Boolean.TRUE.equals(attr.getMultiple())) {
                throw new BadRequestAlertException(
                    "Attribute '" + attr.getName() + "' does not accept multiple values",
                    ENTITY_NAME,
                    "attrmultiple"
                );
            }
        } else {
            incoming.add(value.asText());
        }

        for (String v : incoming) {
            if (!allowed.contains(v)) {
                throw new BadRequestAlertException(
                    "Attribute '" + attr.getName() + "' value '" + v + "' is not in the allowed options",
                    ENTITY_NAME,
                    "attrenum"
                );
            }
        }
    }

    // ---------------------------------------------------------------------
    //  Deletion
    // ---------------------------------------------------------------------

    /**
     * Delete a MegaSetType. Intended only for cleanup of types that have no rows pointing at them.
     * If the type is referenced by any {@link com.nookx.api.domain.MegaSet} the database FK will reject it.
     */
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void delete(Long id) {
        LOG.debug("Request to delete MegaSetType : {}", id);
        megaSetTypeRepository.deleteById(id);
    }
}
