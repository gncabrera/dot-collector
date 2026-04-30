package com.nookx.api.client.service;

import com.nookx.api.client.dto.*;
import com.nookx.api.domain.CloneInformation;
import com.nookx.api.domain.Interest;
import com.nookx.api.domain.Profile;
import com.nookx.api.domain.ProfileCollection;
import com.nookx.api.domain.ProfileCollectionImage;
import com.nookx.api.domain.ProfileCollectionSet;
import com.nookx.api.domain.enumeration.MegaAssetImageSize;
import com.nookx.api.repository.CloneInformationRepository;
import com.nookx.api.repository.CurrencyRepository;
import com.nookx.api.repository.InterestRepository;
import com.nookx.api.repository.ProfileCollectionImageRepository;
import com.nookx.api.repository.ProfileCollectionRepository;
import com.nookx.api.repository.ProfileCollectionSetRepository;
import com.nookx.api.service.ProfileCollectionService;
import com.nookx.api.service.ProfileCollectionSetService;
import com.nookx.api.service.ProfileService;
import com.nookx.api.service.mapper.CurrencyMapper;
import com.nookx.api.service.mapper.InterestMapper;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ClientCollectionService {

    private final ProfileService profileService;
    private final ProfileCollectionService profileCollectionService;
    private final ProfileCollectionRepository profileCollectionRepository;
    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;
    private final CloneInformationRepository cloneInformationRepository;
    private final ProfileCollectionImageRepository profileCollectionImageRepository;
    private final ClientAssetUrlService clientAssetUrlService;
    private final InterestRepository interestRepository;
    private final InterestMapper interestMapper;
    private final ProfileCollectionSetService profileCollectionSetService;
    private final ProfileCollectionSetRepository profileCollectionSetRepository;

    public ClientCollectionService(
        ProfileService profileService,
        ProfileCollectionService profileCollectionService,
        ProfileCollectionRepository profileCollectionRepository,
        CurrencyRepository currencyRepository,
        CurrencyMapper currencyMapper,
        CloneInformationRepository cloneInformationRepository,
        ProfileCollectionImageRepository profileCollectionImageRepository,
        ClientAssetUrlService clientAssetUrlService,
        InterestRepository interestRepository,
        InterestMapper interestMapper,
        ProfileCollectionSetService profileCollectionSetService,
        ProfileCollectionSetRepository profileCollectionSetRepository
    ) {
        this.profileService = profileService;
        this.profileCollectionService = profileCollectionService;
        this.profileCollectionRepository = profileCollectionRepository;
        this.currencyRepository = currencyRepository;
        this.currencyMapper = currencyMapper;
        this.cloneInformationRepository = cloneInformationRepository;
        this.profileCollectionImageRepository = profileCollectionImageRepository;
        this.clientAssetUrlService = clientAssetUrlService;
        this.interestRepository = interestRepository;
        this.interestMapper = interestMapper;
        this.profileCollectionSetService = profileCollectionSetService;
        this.profileCollectionSetRepository = profileCollectionSetRepository;
    }

    @Transactional(readOnly = true)
    public List<ClientCollectionLiteDTO> getUserCollections() {
        Profile currentProfile = profileService.getCurrentProfile();
        if (currentProfile == null) {
            return List.of();
        }
        List<ClientCollectionLiteDTO> collections = profileCollectionRepository
            .findByProfile_Id(currentProfile.getId())
            .stream()
            .map(this::toClientCollectionLiteDTO)
            .toList();
        for (ClientCollectionLiteDTO dto : collections) {
            Long collectionId = dto.getId();
            ClientImageDTO clientImageDto = getClientImageDto(collectionId);
            dto.setImage(clientImageDto);
        }

        return collections;
    }

    private ClientImageDTO getClientImageDto(Long collectionId) {
        return profileCollectionImageRepository
            .findByProfileCollection_Id(collectionId)
            .map(ProfileCollectionImage::getAsset)
            .map(clientAssetUrlService::toClientImageDto)
            .orElse(null);
    }

    @Transactional(readOnly = true)
    public ClientCollectionDTO getCollectionById(Long id) {
        Optional<ProfileCollection> result = profileCollectionRepository.findById(id);
        return result.map(this::toClientCollectionDTO).orElse(null);
    }

    @Transactional
    public ClientCollectionDTO cloneCollection(Long sourceCollectionId, ClientCollectionDTO dto) {
        return profileCollectionRepository
            .findById(sourceCollectionId)
            .map(sourceCollection -> {
                ClientCollectionDTO sourceDto = toClientCollectionDTO(sourceCollection);
                sourceDto.setTitle("Copy of " + sourceDto.getTitle());
                sourceDto.setId(null);
                ClientCollectionDTO clonedCollectionDto = create(sourceDto);

                ProfileCollection clonedCollection = profileCollectionRepository.findById(clonedCollectionDto.getId()).orElseThrow();

                CloneInformation info = new CloneInformation();
                info.setCloned(true);
                info.setSourceCollection(sourceCollection);
                info.setCollection(clonedCollection);
                cloneInformationRepository.save(info);

                clonedCollection.setCloneInformation(info);
                profileCollectionRepository.save(clonedCollection);

                return toClientCollectionDTO(clonedCollection);
            })
            .orElse(null);
    }

    @Transactional
    public ClientCollectionDTO create(ClientCollectionDTO dto) {
        Profile currentProfile = profileService.getCurrentProfile();

        ProfileCollection profileCollection = toProfileCollection(dto);
        profileCollection.setProfile(currentProfile);

        profileCollection = profileCollectionRepository.save(profileCollection);
        return toClientCollectionDTO(profileCollection);
    }

    @Transactional
    public ClientCollectionDTO update(Long id, ClientCollectionUpdateDTO dto) {
        ProfileCollection profileCollection = profileCollectionRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Collection not found", "clientCollection", "idnotfound"));

        profileCollection.setTitle(dto.getTitle());
        profileCollection.setDescription(dto.getDescription());
        profileCollection.setType(dto.getCollectionType());

        ClientCollectionSettingsDTO settings = dto.getSettings();
        if (settings != null) {
            profileCollection.setIsPublic(settings.isPublic());
            profileCollection.setShowPrice(settings.isShowPrice());
            profileCollection.setShowCheckbox(settings.isShowCheckbox());
            profileCollection.setShowComment(settings.isShowComment());
            if (settings.getCurrency() != null && settings.getCurrency().getId() != null) {
                currencyRepository.findById(settings.getCurrency().getId()).ifPresent(profileCollection::setCurrency);
            } else {
                profileCollection.setCurrency(null);
            }
        }

        List<ClientInterestDTO> interestDtos = dto.getInterests();
        if (interestDtos != null && !interestDtos.isEmpty()) {
            Profile current = profileService.getCurrentProfile();
            Long profileId = current.getId();
            Long userId = current.getUser().getId();
            Set<Interest> resolvedInterests = interestDtos
                .stream()
                .filter(interestDto -> interestDto != null && interestDto.getId() != null)
                .map(interestDto ->
                    interestRepository
                        .findByIdLinkedToProfileOrSystem(interestDto.getId(), profileId, userId)
                        .orElseThrow(() -> new BadRequestAlertException("Interest not found", "interest", "idnotfound"))
                )
                .collect(Collectors.toCollection(HashSet::new));
            profileCollection.setInterests(resolvedInterests);
        } else {
            profileCollection.setInterests(new HashSet<>());
        }

        profileCollection = profileCollectionRepository.save(profileCollection);
        return toClientCollectionDTO(profileCollection);
    }

    /**
     * Delete a client collection along with its dependent data:
     * <ul>
     *   <li>Removes its associated image (if any).</li>
     *   <li>Clears its interest relations (without deleting the interests).</li>
     *   <li>Nulls out {@code sourceCollection} on any {@link CloneInformation} that references this collection
     *       and deletes the {@link CloneInformation} that this collection itself owns (if it was a clone).</li>
     *   <li>Deletes every {@link ProfileCollectionSet} belonging to this collection.</li>
     *   <li>Leaves {@code profile} and {@code currency} untouched.</li>
     * </ul>
     */
    @Transactional
    public void delete(Long id) {
        ProfileCollection collection = profileCollectionRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Collection not found", "clientCollection", "idnotfound"));

        Profile currentProfile = profileService.getCurrentProfile();
        if (currentProfile == null || collection.getProfile() == null || !currentProfile.getId().equals(collection.getProfile().getId())) {
            throw new BadRequestAlertException("Forbidden", "clientCollection", "forbidden");
        }

        if (collection.getImage() != null) {
            collection.setImage(null);
        }

        collection.getInterests().clear();
        collection = profileCollectionRepository.save(collection);

        List<CloneInformation> referencingAsSource = cloneInformationRepository.findBySourceCollection_Id(id);
        for (CloneInformation info : referencingAsSource) {
            info.setSourceCollection(null);
            cloneInformationRepository.save(info);
        }

        CloneInformation ownedCloneInformation = collection.getCloneInformation();
        if (ownedCloneInformation != null) {
            collection.setCloneInformation(null);
            collection = profileCollectionRepository.save(collection);
            cloneInformationRepository.delete(ownedCloneInformation);
        }

        List<ProfileCollectionSet> collectionSets = profileCollectionSetRepository.findByCollection_Id(id);
        for (ProfileCollectionSet set : collectionSets) {
            profileCollectionSetService.delete(set.getId());
        }

        profileCollectionService.delete(id);
    }

    private ClientCollectionDTO toClientCollectionDTO(ProfileCollection profileCollection) {
        ClientCollectionDTO dto = new ClientCollectionDTO();
        dto.setId(profileCollection.getId());
        dto.setTitle(profileCollection.getTitle());
        dto.setDescription(profileCollection.getDescription());
        dto.setCollectionType(profileCollection.getType());

        ClientCollectionSettingsDTO settings = new ClientCollectionSettingsDTO();
        if (profileCollection.getIsPublic() != null) {
            settings.setPublic(profileCollection.getIsPublic());
        }
        settings.setShowPrice(profileCollection.isShowPrice());
        settings.setShowCheckbox(profileCollection.isShowCheckbox());
        settings.setShowComment(profileCollection.isShowComment());
        settings.setCurrency(currencyMapper.toDto(profileCollection.getCurrency()));
        dto.setSettings(settings);

        if (profileCollection.getProfile() != null) {
            var profile = profileCollection.getProfile();
            if (profile.getUsername() != null) {
                dto.setCreatedBy(profile.getUsername());
            } else if (profile.getFullName() != null) {
                dto.setCreatedBy(profile.getFullName());
            }
        }

        ClientCollectionCommunityDTO communityDTO = getClientCollectionCommunityDTO(profileCollection);

        CloneInformation cloneInformation = profileCollection.getCloneInformation();
        if (cloneInformation != null) {
            ClientCloneInformationDTO clone = getClientCloneInformationDTO(cloneInformation);
            communityDTO.setClone(clone);
        }
        dto.setCommunity(communityDTO);

        ClientImageDTO clientImageDto = getClientImageDto(profileCollection.getId());
        dto.setImage(clientImageDto);

        dto.setInterests(toClientInterestDtos(profileCollection));

        return dto;
    }

    private ClientCollectionCommunityDTO getClientCollectionCommunityDTO(ProfileCollection collection) {
        ClientCollectionCommunityDTO communityDTO = new ClientCollectionCommunityDTO();
        communityDTO.setTotalCloned(77);
        communityDTO.setTotalComments(88);
        communityDTO.setTotalStars(99);
        return communityDTO;
    }

    private List<ClientInterestDTO> toClientInterestDtos(ProfileCollection profileCollection) {
        if (profileCollection.getInterests() == null || profileCollection.getInterests().isEmpty()) {
            return List.of();
        }
        return profileCollection.getInterests().stream().map(interestMapper::toDto).collect(Collectors.toList());
    }

    private static ClientCloneInformationDTO getClientCloneInformationDTO(CloneInformation cloneInformation) {
        ClientCloneInformationDTO clone = new ClientCloneInformationDTO();
        clone.setCloned(cloneInformation.getCloned());
        clone.setSourceCollectionId(cloneInformation.getSourceCollection().getId());
        clone.setSourceUserId(cloneInformation.getSourceCollection().getProfile().getId());
        clone.setSourceUsername(cloneInformation.getSourceCollection().getProfile().getUsername());
        //TODO: Add Image here!
        clone.setSourceUserImageUrl("https://placehold.co/100x100");
        return clone;
    }

    private ClientCollectionLiteDTO toClientCollectionLiteDTO(ProfileCollection profileCollection) {
        ClientCollectionLiteDTO dto = new ClientCollectionLiteDTO();
        dto.setId(profileCollection.getId());
        dto.setTitle(profileCollection.getTitle());
        dto.setDescription(profileCollection.getDescription());
        dto.setCollectionType(profileCollection.getType());

        if (profileCollection.getProfile() != null) {
            var profile = profileCollection.getProfile();
            if (profile.getUsername() != null) {
                dto.setCreatedBy(profile.getUsername());
            } else if (profile.getFullName() != null) {
                dto.setCreatedBy(profile.getFullName());
            }
        }

        ClientCollectionCommunityDTO communityDTO = getClientCollectionCommunityDTO(profileCollection);
        dto.setCommunity(communityDTO);
        dto.setInterests(toClientInterestDtos(profileCollection));

        return dto;
    }

    private ProfileCollection toProfileCollection(ClientCollectionDTO dto) {
        ProfileCollection entity = new ProfileCollection();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setType(dto.getCollectionType());

        ClientCollectionSettingsDTO settings = dto.getSettings();
        if (settings != null) {
            entity.setIsPublic(settings.isPublic());
            entity.setShowPrice(settings.isShowPrice());
            entity.setShowCheckbox(settings.isShowCheckbox());
            entity.setShowComment(settings.isShowComment());
            if (settings.getCurrency() != null && settings.getCurrency().getId() != null) {
                currencyRepository.findById(settings.getCurrency().getId()).ifPresent(entity::setCurrency);
            }
        }

        List<ClientInterestDTO> interestDtos = dto.getInterests();
        if (interestDtos != null && !interestDtos.isEmpty()) {
            Profile current = profileService.getCurrentProfile();
            Long profileId = current.getId();
            Long userId = current.getUser().getId();
            Set<Interest> resolvedInterests = interestDtos
                .stream()
                .filter(interestDto -> interestDto != null && interestDto.getId() != null)
                .map(interestDto ->
                    interestRepository
                        .findByIdLinkedToProfileOrSystem(interestDto.getId(), profileId, userId)
                        .orElseThrow(() -> new BadRequestAlertException("Interest not found", "interest", "idnotfound"))
                )
                .collect(Collectors.toCollection(HashSet::new));
            entity.setInterests(resolvedInterests);
        } else {
            entity.setInterests(new HashSet<>());
        }

        return entity;
    }
}
