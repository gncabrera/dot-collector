package com.nookx.api.service;

import com.nookx.api.domain.MegaAttribute;
import com.nookx.api.domain.MegaAttributeOption;
import com.nookx.api.domain.enumeration.AttributeType;
import com.nookx.api.domain.enumeration.UIComponent;
import com.nookx.api.repository.MegaAttributeOptionRepository;
import com.nookx.api.repository.MegaAttributeRepository;
import com.nookx.api.service.dto.MegaAttributeDTO;
import com.nookx.api.service.mapper.MegaAttributeMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link MegaAttribute}.
 *
 * <p>This service is intended for managing the catalog of reusable attributes.
 * Attribute schemas attached to a {@link com.nookx.api.domain.MegaSetType} should
 * never be edited in place; instead use {@link MegaSetTypeService#createNewVersion}
 * to evolve the schema and keep historical data working.</p>
 */
@Service
@Transactional
public class MegaAttributeService {

    private static final Logger LOG = LoggerFactory.getLogger(MegaAttributeService.class);

    private final MegaAttributeRepository megaAttributeRepository;

    private final MegaAttributeOptionRepository megaAttributeOptionRepository;

    private final MegaAttributeMapper megaAttributeMapper;

    public MegaAttributeService(
        MegaAttributeRepository megaAttributeRepository,
        MegaAttributeOptionRepository megaAttributeOptionRepository,
        MegaAttributeMapper megaAttributeMapper
    ) {
        this.megaAttributeRepository = megaAttributeRepository;
        this.megaAttributeOptionRepository = megaAttributeOptionRepository;
        this.megaAttributeMapper = megaAttributeMapper;
    }

    public MegaAttributeDTO save(MegaAttributeDTO megaAttributeDTO) {
        LOG.debug("Request to save MegaAttribute : {}", megaAttributeDTO);
        MegaAttribute megaAttribute = megaAttributeMapper.toEntity(megaAttributeDTO);
        if (megaAttribute.getUiComponent() == null) {
            megaAttribute.setUiComponent(defaultUiComponentFor(megaAttribute.getType()));
        }
        megaAttribute = megaAttributeRepository.save(megaAttribute);
        return megaAttributeMapper.toDto(megaAttribute);
    }

    @Transactional(readOnly = true)
    public List<MegaAttributeDTO> findAll() {
        LOG.debug("Request to get all MegaAttributes");
        return megaAttributeRepository.findAll().stream().map(megaAttributeMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Transactional(readOnly = true)
    public Optional<MegaAttributeDTO> findOne(Long id) {
        LOG.debug("Request to get MegaAttribute : {}", id);
        return megaAttributeRepository.findById(id).map(megaAttributeMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete MegaAttribute : {}", id);
        megaAttributeRepository.deleteById(id);
    }

    /**
     * Default UI component to use for a given attribute type when none is set.
     * Used to drive the dynamic form generation on the frontend.
     */
    public UIComponent defaultUiComponentFor(AttributeType type) {
        if (type == null) {
            return UIComponent.INPUT;
        }
        return switch (type) {
            case BOOLEAN -> UIComponent.CHECKBOX;
            case ENUM -> UIComponent.SELECT;
            case DATE -> UIComponent.DATE;
            case NUMBER -> UIComponent.NUMBER;
            case STRING -> UIComponent.INPUT;
        };
    }

    /**
     * Clone an attribute without an id, copying schema fields and (persisted) options.
     * The returned entity is detached and ready to be saved against a new MegaSetType version.
     */
    public MegaAttribute cloneAttribute(MegaAttribute source) {
        MegaAttribute copy = new MegaAttribute()
            .name(source.getName())
            .label(source.getLabel())
            .description(source.getDescription())
            .uiComponent(source.getUiComponent())
            .type(source.getType())
            .required(source.getRequired())
            .multiple(source.getMultiple())
            .defaultValue(source.getDefaultValue())
            .minNumber(source.getMinNumber())
            .maxNumber(source.getMaxNumber())
            .minLength(source.getMinLength())
            .maxLength(source.getMaxLength())
            .regex(source.getRegex())
            .order(source.getOrder())
            .attributeGroup(source.getAttributeGroup())
            .active(source.getActive());

        MegaAttribute saved = megaAttributeRepository.save(copy);

        if (source.getId() != null) {
            List<MegaAttributeOption> options = megaAttributeOptionRepository.findByAttribute_Id(source.getId());
            for (MegaAttributeOption opt : options) {
                MegaAttributeOption optCopy = new MegaAttributeOption()
                    .label(opt.getLabel())
                    .value(opt.getValue())
                    .description(opt.getDescription())
                    .attribute(saved);
                megaAttributeOptionRepository.save(optCopy);
            }
        }
        return saved;
    }

    /**
     * Apply a non-id-bearing patch from a DTO onto an existing (detached) attribute.
     * Used while merging attributes during {@link MegaSetTypeService#createNewVersion}.
     */
    public void applyPatch(MegaAttribute target, MegaAttributeDTO patch) {
        if (patch.getLabel() != null) target.setLabel(patch.getLabel());
        if (patch.getDescription() != null) target.setDescription(patch.getDescription());
        if (patch.getUiComponent() != null) target.setUiComponent(patch.getUiComponent());
        if (patch.getType() != null) target.setType(patch.getType());
        if (patch.getRequired() != null) target.setRequired(patch.getRequired());
        if (patch.getMultiple() != null) target.setMultiple(patch.getMultiple());
        if (patch.getDefaultValue() != null) target.setDefaultValue(patch.getDefaultValue());
        if (patch.getMinNumber() != null) target.setMinNumber(patch.getMinNumber());
        if (patch.getMaxNumber() != null) target.setMaxNumber(patch.getMaxNumber());
        if (patch.getMinLength() != null) target.setMinLength(patch.getMinLength());
        if (patch.getMaxLength() != null) target.setMaxLength(patch.getMaxLength());
        if (patch.getRegex() != null) target.setRegex(patch.getRegex());
        if (patch.getOrder() != null) target.setOrder(patch.getOrder());
        if (patch.getAttributeGroup() != null) target.setAttributeGroup(patch.getAttributeGroup());
        if (patch.getActive() != null) target.setActive(patch.getActive());
    }

    /**
     * Build a new attribute entity from a DTO, ignoring any incoming id and relations.
     * Used to add brand-new attributes when creating a new MegaSetType version.
     */
    public MegaAttribute fromDto(MegaAttributeDTO dto) {
        MegaAttribute a = new MegaAttribute()
            .name(dto.getName())
            .label(dto.getLabel())
            .description(dto.getDescription())
            .uiComponent(dto.getUiComponent() != null ? dto.getUiComponent() : defaultUiComponentFor(dto.getType()))
            .type(dto.getType())
            .required(dto.getRequired())
            .multiple(dto.getMultiple())
            .defaultValue(dto.getDefaultValue())
            .minNumber(dto.getMinNumber())
            .maxNumber(dto.getMaxNumber())
            .minLength(dto.getMinLength())
            .maxLength(dto.getMaxLength())
            .regex(dto.getRegex())
            .order(dto.getOrder())
            .attributeGroup(dto.getAttributeGroup())
            .active(dto.getActive() != null ? dto.getActive() : Boolean.TRUE);
        return megaAttributeRepository.save(a);
    }

    @Transactional(readOnly = true)
    public List<MegaAttributeOption> findOptions(Long attributeId) {
        return megaAttributeOptionRepository.findByAttribute_Id(attributeId);
    }
}
