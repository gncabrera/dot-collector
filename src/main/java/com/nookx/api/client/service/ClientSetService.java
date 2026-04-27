package com.nookx.api.client.service;

import com.nookx.api.client.dto.ClientSetCommunityDTO;
import com.nookx.api.client.dto.ClientSetDTO;
import com.nookx.api.client.dto.ClientSetLiteDTO;
import com.nookx.api.domain.MegaSet;
import com.nookx.api.repository.MegaSetRepository;
import com.nookx.api.repository.ProfileCollectionSetRepository;
import com.nookx.api.service.MegaSetService;
import com.nookx.api.service.MegaSetTypeService;
import com.nookx.api.service.dto.MegaSetDTO;
import com.nookx.api.service.mapper.MegaSetMapper;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Client-facing facade for {@link MegaSet}.
 *
 * <p>Reads load the entity through {@link MegaSetRepository} and enrich it with community
 * stats, image URLs and file uuids. Write paths (create / update / delete) are delegated
 * to {@link MegaSetService} using {@link MegaSetDTO} as the intermediate representation –
 * {@link #toMegaSetDto(ClientSetDTO)} and {@link #toClientSetDto(MegaSetDTO)} are the only
 * bridge between the client DTO and the catalog DTO.</p>
 */
@Slf4j
@Service
@Transactional
public class ClientSetService {

    private static final String ENTITY_NAME = "clientSet";

    private final MegaSetService megaSetService;
    private final MegaSetRepository megaSetRepository;
    private final MegaSetMapper megaSetMapper;
    private final MegaSetTypeService megaSetTypeService;
    private final ClientSetAssetService clientSetAssetService;
    private final ProfileCollectionSetRepository profileCollectionSetRepository;

    public ClientSetService(
        MegaSetService megaSetService,
        MegaSetRepository megaSetRepository,
        MegaSetMapper megaSetMapper,
        MegaSetTypeService megaSetTypeService,
        ClientSetAssetService clientSetAssetService,
        ProfileCollectionSetRepository profileCollectionSetRepository
    ) {
        this.megaSetService = megaSetService;
        this.megaSetRepository = megaSetRepository;
        this.megaSetMapper = megaSetMapper;
        this.megaSetTypeService = megaSetTypeService;
        this.clientSetAssetService = clientSetAssetService;
        this.profileCollectionSetRepository = profileCollectionSetRepository;
    }

    // -----------------------------------------------------------------
    //  Public API
    // -----------------------------------------------------------------

    @Transactional(readOnly = true)
    public Optional<ClientSetDTO> getById(Long id) {
        log.debug("Request to get ClientSet : {}", id);
        return megaSetRepository.findById(id).map(megaSetMapper::toDto).map(this::enrich);
    }

    public ClientSetDTO create(ClientSetDTO dto) {
        log.debug("Request to create ClientSet : {}", dto);
        MegaSetDTO saved = megaSetService.save(toMegaSetDto(dto));
        return enrich(saved);
    }

    public ClientSetDTO update(Long id, ClientSetDTO dto) {
        log.debug("Request to update ClientSet {} : {}", id, dto);
        dto.setId(id);
        MegaSetDTO updated = megaSetService.update(toMegaSetDto(dto));
        return enrich(updated);
    }

    public void delete(Long id) {
        log.debug("Request to delete ClientSet : {}", id);
        megaSetService.delete(id);
    }

    /**
     * Get every set that is part of the given collection, fully enriched
     * (community stats, image URLs, file uuids and detailed type info).
     *
     * <p>This method does NOT enforce any authorization on the collection –
     * callers are responsible for verifying that the current user is allowed
     * to read the collection before invoking it.</p>
     */
    @Transactional(readOnly = true)
    public List<ClientSetLiteDTO> getSetsByCollectionId(Long collectionId) {
        log.debug("Request to get sets for collection : {}", collectionId);
        return profileCollectionSetRepository.findSetsByCollectionId(collectionId).stream().map(this::toLiteDto).toList();
    }

    private ClientSetLiteDTO toLiteDto(MegaSet source) {
        if (source == null) {
            return null;
        }
        ClientSetLiteDTO dto = new ClientSetLiteDTO();
        dto.setId(source.getId());
        dto.setSetNumber(source.getSetNumber());
        dto.setName(source.getName());
        dto.setImage(clientSetAssetService.getPrimaryImage(dto.getId()));
        return dto;
    }

    // -----------------------------------------------------------------
    //  ClientSetDTO <-> MegaSetDTO
    // -----------------------------------------------------------------

    /**
     * Copy every field that exists in both DTOs. The fields unique to {@link ClientSetDTO}
     * (community, images, files) are computed separately by {@link #enrich(MegaSetDTO)}.
     */
    public ClientSetDTO toClientSetDto(MegaSetDTO source) {
        if (source == null) {
            return null;
        }
        ClientSetDTO dto = new ClientSetDTO();
        dto.setId(source.getId());
        dto.setSetNumber(source.getSetNumber());
        dto.setNotes(source.getNotes());
        dto.setName(source.getName());
        dto.setDescription(source.getDescription());
        dto.setPublicItem(source.isPublicItem());
        dto.setAttributes(source.getAttributes());
        dto.setType(source.getType());
        dto.setInterest(source.getInterest());
        return dto;
    }

    /**
     * Reverse of {@link #toClientSetDto(MegaSetDTO)} – used to feed {@link MegaSetService}
     * on every write path.
     */
    public MegaSetDTO toMegaSetDto(ClientSetDTO source) {
        if (source == null) {
            return null;
        }
        MegaSetDTO dto = new MegaSetDTO();
        dto.setId(source.getId());
        dto.setSetNumber(source.getSetNumber());
        dto.setNotes(source.getNotes());
        dto.setName(source.getName());
        dto.setDescription(source.getDescription());
        dto.setPublicItem(source.isPublicItem());
        dto.setAttributes(source.getAttributes());
        dto.setType(source.getType());
        dto.setInterest(source.getInterest());
        return dto;
    }

    // -----------------------------------------------------------------
    //  Enrichment
    // -----------------------------------------------------------------

    /**
     * Build a fully populated {@link ClientSetDTO} from a {@link MegaSetDTO}:
     * the catalog DTO carries the basic fields, while community stats, image URLs,
     * file uuids and the full (attribute-bearing) type DTO are loaded here.
     */
    private ClientSetDTO enrich(MegaSetDTO source) {
        ClientSetDTO dto = toClientSetDto(source);
        if (dto == null || dto.getId() == null) {
            return dto;
        }
        MegaSet entity = megaSetRepository
            .findById(dto.getId())
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));

        if (entity.getType() != null) {
            dto.setType(megaSetTypeService.toDto(entity.getType()));
        }
        dto.setCommunity(buildCommunityDto(dto.getId()));
        dto.setImages(clientSetAssetService.getImages(dto.getId()));
        dto.setFiles(clientSetAssetService.getFiles(dto.getId()));
        return dto;
    }

    private ClientSetCommunityDTO buildCommunityDto(Long setId) {
        ClientSetCommunityDTO community = new ClientSetCommunityDTO();
        community.setTotalOwned(profileCollectionSetRepository.countDistinctOwnersBySetId(setId));
        community.setTotalWanted(profileCollectionSetRepository.countDistinctWantersBySetId(setId));
        community.setTotalCollectionsContaining(profileCollectionSetRepository.countDistinctCollectionsBySetId(setId));
        return community;
    }
}
