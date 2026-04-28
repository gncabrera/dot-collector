package com.nookx.api.client.service;

import com.nookx.api.client.dto.ClientCollectionsSummaryDTO;
import com.nookx.api.client.dto.ClientContactLinksDTO;
import com.nookx.api.client.dto.ClientImageDTO;
import com.nookx.api.client.dto.ClientInterestDTO;
import com.nookx.api.client.dto.ClientProfileDTO;
import com.nookx.api.client.dto.ClientProfileLiteDTO;
import com.nookx.api.domain.Profile;
import com.nookx.api.domain.ProfileImage;
import com.nookx.api.domain.enumeration.ProfileCollectionType;
import com.nookx.api.repository.ProfileCollectionRepository;
import com.nookx.api.repository.ProfileImageRepository;
import com.nookx.api.repository.ProfileRepository;
import com.nookx.api.service.InterestService;
import com.nookx.api.service.ProfileService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ClientProfileService {

    private final ProfileService profileService;
    private final ProfileRepository profileRepository;
    private final ProfileCollectionRepository profileCollectionRepository;
    private final ProfileImageRepository profileImageRepository;
    private final ClientAssetUrlService clientAssetUrlService;
    private final InterestService interestService;

    public ClientProfileService(
        ProfileService profileService,
        ProfileRepository profileRepository,
        ProfileCollectionRepository profileCollectionRepository,
        ProfileImageRepository profileImageRepository,
        ClientAssetUrlService clientAssetUrlService,
        InterestService interestService
    ) {
        this.profileService = profileService;
        this.profileRepository = profileRepository;
        this.profileCollectionRepository = profileCollectionRepository;
        this.profileImageRepository = profileImageRepository;
        this.clientAssetUrlService = clientAssetUrlService;
        this.interestService = interestService;
    }

    @Transactional(readOnly = true)
    public ClientProfileDTO getProfile() {
        log.debug("Request to get current ClientProfile");
        Profile profile = profileService.getCurrentProfile();
        return toClientProfileDTO(profile);
    }

    @Transactional(readOnly = true)
    public ClientProfileLiteDTO getProfileLite() {
        log.debug("Request to get current ClientProfileLite");
        Profile profile = profileService.getCurrentProfile();
        ClientProfileLiteDTO dto = new ClientProfileLiteDTO();
        dto.setName(resolveName(profile));
        dto.setImage(getProfileImage(profile));
        dto.setInterests(interestService.findAllForCurrentProfile());
        return dto;
    }

    public ClientProfileDTO update(ClientProfileDTO clientProfileDTO) {
        log.debug("Request to update current ClientProfile : {}", clientProfileDTO);
        Profile profile = profileService.getCurrentProfile();

        profile.setUsername(clientProfileDTO.getUsername());
        profile.setLocation(clientProfileDTO.getLocation());

        ClientContactLinksDTO contactLinks = clientProfileDTO.getContactLinks();
        if (contactLinks != null) {
            profile.setEmail(contactLinks.getEmail());
            profile.setInstagram(contactLinks.getInstagram());
            profile.setFacebook(contactLinks.getFacebook());
            profile.setWhatsapp(contactLinks.getWhatsapp());
        }

        profile = profileRepository.save(profile);
        return toClientProfileDTO(profile);
    }

    private ClientProfileDTO toClientProfileDTO(Profile profile) {
        ClientProfileDTO dto = new ClientProfileDTO();
        dto.setUsername(profile.getUsername());
        dto.setLocation(profile.getLocation());
        dto.setImage(getProfileImage(profile));
        dto.setCollectionsSummary(buildCollectionsSummary(profile));
        dto.setContactLinks(buildContactLinks(profile));
        dto.setInterests(getInterestsForProfile());
        return dto;
    }

    private ClientImageDTO getProfileImage(Profile profile) {
        return profileImageRepository
            .findByProfile_Id(profile.getId())
            .map(ProfileImage::getAsset)
            .map(clientAssetUrlService::toClientImageDto)
            .orElse(null);
    }

    private ClientCollectionsSummaryDTO buildCollectionsSummary(Profile profile) {
        ClientCollectionsSummaryDTO summary = new ClientCollectionsSummaryDTO();
        Long profileId = profile.getId();
        summary.setToSell(profileCollectionRepository.countByProfile_IdAndType(profileId, ProfileCollectionType.TO_SELL));
        summary.setStandard(profileCollectionRepository.countByProfile_IdAndType(profileId, ProfileCollectionType.STANDARD));
        return summary;
    }

    private ClientContactLinksDTO buildContactLinks(Profile profile) {
        ClientContactLinksDTO contactLinks = new ClientContactLinksDTO();
        contactLinks.setEmail(profile.getEmail());
        contactLinks.setInstagram(profile.getInstagram());
        contactLinks.setFacebook(profile.getFacebook());
        contactLinks.setWhatsapp(profile.getWhatsapp());
        return contactLinks;
    }

    private List<ClientInterestDTO> getInterestsForProfile() {
        return interestService.findAllForCurrentProfile();
    }

    private String resolveName(Profile profile) {
        if (profile.getUsername() != null) {
            return profile.getUsername();
        }
        return profile.getFullName();
    }
}
