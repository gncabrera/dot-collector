package com.nookx.api.client.service;

import com.nookx.api.client.dto.ClientBlockedProfileDTO;
import com.nookx.api.client.dto.ClientCollectionsSummaryDTO;
import com.nookx.api.client.dto.ClientContactLinksDTO;
import com.nookx.api.client.dto.ClientImageDTO;
import com.nookx.api.client.dto.ClientProfileCommunityDTO;
import com.nookx.api.client.dto.ClientProfileDTO;
import com.nookx.api.client.dto.ClientProfileLiteDTO;
import com.nookx.api.domain.BlockedProfile;
import com.nookx.api.domain.FollowingProfile;
import com.nookx.api.domain.Profile;
import com.nookx.api.domain.ProfileImage;
import com.nookx.api.domain.User;
import com.nookx.api.domain.enumeration.ProfileCollectionType;
import com.nookx.api.repository.BlockedProfileRepository;
import com.nookx.api.repository.FollowingProfileRepository;
import com.nookx.api.repository.ProfileCollectionRepository;
import com.nookx.api.repository.ProfileImageRepository;
import com.nookx.api.repository.ProfileRepository;
import com.nookx.api.service.InterestService;
import com.nookx.api.service.ProfileService;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import java.time.LocalDate;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class ClientProfileService {

    private static final String ENTITY_NAME = "clientProfile";

    private final ProfileService profileService;
    private final ProfileRepository profileRepository;
    private final ProfileCollectionRepository profileCollectionRepository;
    private final ProfileImageRepository profileImageRepository;
    private final ClientAssetUrlService clientAssetUrlService;
    private final InterestService interestService;
    private final FollowingProfileRepository followingProfileRepository;
    private final BlockedProfileRepository blockedProfileRepository;

    public ClientProfileService(
        ProfileService profileService,
        ProfileRepository profileRepository,
        ProfileCollectionRepository profileCollectionRepository,
        ProfileImageRepository profileImageRepository,
        ClientAssetUrlService clientAssetUrlService,
        InterestService interestService,
        FollowingProfileRepository followingProfileRepository,
        BlockedProfileRepository blockedProfileRepository
    ) {
        this.profileService = profileService;
        this.profileRepository = profileRepository;
        this.profileCollectionRepository = profileCollectionRepository;
        this.profileImageRepository = profileImageRepository;
        this.clientAssetUrlService = clientAssetUrlService;
        this.interestService = interestService;
        this.followingProfileRepository = followingProfileRepository;
        this.blockedProfileRepository = blockedProfileRepository;
    }

    @Transactional(readOnly = true)
    public ClientProfileDTO getProfile() {
        log.debug("Request to get current ClientProfile");
        Profile profile = profileService.getCurrentProfile();
        return toClientProfileDTO(profile);
    }

    /**
     * Get a profile by id from the perspective of the current user.
     *
     * <ul>
     *   <li>If {@code id} matches the current profile, return the full DTO.</li>
     *   <li>If a block exists in either direction between current and target, throw {@code 404}.</li>
     *   <li>If the target is not public, return a limited view (username, image, publicProfile=false).</li>
     *   <li>Otherwise, return the full DTO.</li>
     * </ul>
     *
     * @param id Profile id.
     * @return the {@link ClientProfileDTO} (full or limited).
     * @throws ResponseStatusException 404 if target does not exist or is mutually blocked.
     */
    @Transactional(readOnly = true)
    public ClientProfileDTO getProfileById(Long id) {
        log.debug("Request to get ClientProfile : {}", id);
        Profile target = profileRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Profile current = profileService.getCurrentProfile();
        if (current != null && Objects.equals(current.getId(), target.getId())) {
            return toClientProfileDTO(target);
        }

        if (current != null && isBlockedEitherWay(current.getId(), target.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (!Boolean.TRUE.equals(target.getPublicProfile())) {
            return toLimitedClientProfileDTO(target);
        }

        return toClientProfileDTO(target);
    }

    @Transactional(readOnly = true)
    public ClientProfileLiteDTO getProfileLite() {
        log.debug("Request to get current ClientProfileLite");
        Profile profile = profileService.getCurrentProfile();
        ClientProfileLiteDTO dto = new ClientProfileLiteDTO();
        dto.setId(profile.getId());
        dto.setName(resolveName(profile));
        dto.setImage(getProfileImage(profile));
        dto.setPublicProfile(profile.getPublicProfile());
        dto.setInterests(interestService.findAllForCurrentProfile());
        return dto;
    }

    @Transactional(readOnly = true)
    public ClientProfileLiteDTO getProfileLite(User user, boolean loadInterests) {
        log.debug("Request to get ClientProfileLite by User");
        if (user == null) {
            return null;
        }
        Profile profile = profileRepository.findByUserId(user.getId()).orElse(null);
        if (profile == null) {
            return null;
        }
        ClientProfileLiteDTO dto = new ClientProfileLiteDTO();
        dto.setId(profile.getId());
        dto.setName(resolveName(profile));
        dto.setImage(getProfileImage(profile));
        dto.setPublicProfile(profile.getPublicProfile());
        if (loadInterests) {
            dto.setInterests(interestService.findAllForCurrentProfile());
        }
        return dto;
    }

    public ClientProfileDTO update(ClientProfileDTO clientProfileDTO) {
        log.debug("Request to update current ClientProfile : {}", clientProfileDTO);
        Profile profile = profileService.getCurrentProfile();

        profile.setUsername(clientProfileDTO.getUsername());
        profile.setLocation(clientProfileDTO.getLocation());
        if (clientProfileDTO.getPublicProfile() != null) {
            profile.setPublicProfile(clientProfileDTO.getPublicProfile());
        }

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

    /**
     * Follow a target profile on behalf of the current profile.
     *
     * <p>Idempotent: if already following, this is a no-op.
     *
     * @param targetProfileId id of the profile to follow.
     * @throws BadRequestAlertException 400 when following self.
     * @throws ResponseStatusException 404 when target does not exist or is mutually blocked.
     */
    @Transactional
    public void follow(Long targetProfileId) {
        log.debug("Request to follow Profile : {}", targetProfileId);
        Profile current = requireCurrentProfile();
        if (Objects.equals(current.getId(), targetProfileId)) {
            throw new BadRequestAlertException("Cannot follow self", ENTITY_NAME, "followself");
        }
        Profile target = profileRepository.findById(targetProfileId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (isBlockedEitherWay(current.getId(), target.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (followingProfileRepository.existsByProfile_IdAndFollowedProfile_Id(current.getId(), target.getId())) {
            return;
        }

        FollowingProfile entity = new FollowingProfile().profile(current).followedProfile(target).dateFollowing(LocalDate.now());
        followingProfileRepository.save(entity);
    }

    /**
     * Unfollow a target profile on behalf of the current profile. Idempotent: missing rows are a no-op.
     *
     * @param targetProfileId id of the profile to unfollow.
     * @throws BadRequestAlertException 400 when unfollowing self.
     */
    @Transactional
    public void unfollow(Long targetProfileId) {
        log.debug("Request to unfollow Profile : {}", targetProfileId);
        Profile current = requireCurrentProfile();
        if (Objects.equals(current.getId(), targetProfileId)) {
            throw new BadRequestAlertException("Cannot unfollow self", ENTITY_NAME, "unfollowself");
        }
        followingProfileRepository.deleteByProfile_IdAndFollowedProfile_Id(current.getId(), targetProfileId);
    }

    /**
     * Block a target profile on behalf of the current profile.
     *
     * <p>Idempotent: if already blocked, the {@code reason} is updated. Removes follow rows in both directions.
     *
     * @param targetProfileId id of the profile to block.
     * @param dto block payload (optional reason).
     * @throws BadRequestAlertException 400 when blocking self.
     * @throws ResponseStatusException 404 when target does not exist.
     */
    @Transactional
    public void block(Long targetProfileId, ClientBlockedProfileDTO dto) {
        log.debug("Request to block Profile : {} with {}", targetProfileId, dto);
        Profile current = requireCurrentProfile();
        if (Objects.equals(current.getId(), targetProfileId)) {
            throw new BadRequestAlertException("Cannot block self", ENTITY_NAME, "blockself");
        }
        Profile target = profileRepository.findById(targetProfileId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String reason = dto != null ? dto.getReason() : null;

        BlockedProfile entity = blockedProfileRepository
            .findByProfile_IdAndBlockedProfile_Id(current.getId(), target.getId())
            .orElseGet(() -> new BlockedProfile().profile(current).blockedProfile(target));
        entity.setReason(reason);
        if (entity.getDateBlocked() == null) {
            entity.setDateBlocked(LocalDate.now());
        }
        blockedProfileRepository.save(entity);

        followingProfileRepository.deleteByProfile_IdAndFollowedProfile_IdOrProfile_IdAndFollowedProfile_Id(
            current.getId(),
            target.getId(),
            target.getId(),
            current.getId()
        );
    }

    /**
     * Unblock a target profile on behalf of the current profile. Idempotent: missing rows are a no-op.
     *
     * @param targetProfileId id of the profile to unblock.
     * @throws BadRequestAlertException 400 when unblocking self.
     */
    @Transactional
    public void unblock(Long targetProfileId) {
        log.debug("Request to unblock Profile : {}", targetProfileId);
        Profile current = requireCurrentProfile();
        if (Objects.equals(current.getId(), targetProfileId)) {
            throw new BadRequestAlertException("Cannot unblock self", ENTITY_NAME, "unblockself");
        }
        blockedProfileRepository.deleteByProfile_IdAndBlockedProfile_Id(current.getId(), targetProfileId);
    }

    private boolean isBlockedEitherWay(Long profileIdA, Long profileIdB) {
        return blockedProfileRepository.existsByProfile_IdAndBlockedProfile_IdOrProfile_IdAndBlockedProfile_Id(
            profileIdA,
            profileIdB,
            profileIdB,
            profileIdA
        );
    }

    private Profile requireCurrentProfile() {
        Profile current = profileService.getCurrentProfile();
        if (current == null) {
            throw new BadRequestAlertException("No current profile", ENTITY_NAME, "noprofile");
        }
        return current;
    }

    private ClientProfileDTO toClientProfileDTO(Profile profile) {
        ClientProfileDTO dto = new ClientProfileDTO();
        dto.setId(profile.getId());
        dto.setUsername(profile.getUsername());
        dto.setLocation(profile.getLocation());
        dto.setPublicProfile(profile.getPublicProfile());
        dto.setImage(getProfileImage(profile));
        dto.setCollectionsSummary(buildCollectionsSummary(profile));
        dto.setContactLinks(buildContactLinks(profile));
        dto.setCommunityDTO(buildCommunity(profile));
        dto.setInterests(interestService.findAllForProfile(profile.getId()));
        return dto;
    }

    private ClientProfileDTO toLimitedClientProfileDTO(Profile profile) {
        ClientProfileDTO dto = new ClientProfileDTO();
        dto.setId(profile.getId());
        dto.setUsername(profile.getUsername());
        dto.setImage(getProfileImage(profile));
        dto.setPublicProfile(profile.getPublicProfile());
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

    /**
     * Build the community section for a profile. Counts ignore follow rows that have any block relationship
     * with the profile in either direction.
     */
    private ClientProfileCommunityDTO buildCommunity(Profile profile) {
        ClientProfileCommunityDTO community = new ClientProfileCommunityDTO();
        Long profileId = profile.getId();
        community.setFollowers((int) followingProfileRepository.countFollowersExcludingBlocked(profileId));
        community.setFollowing((int) followingProfileRepository.countFollowingExcludingBlocked(profileId));
        return community;
    }

    private String resolveName(Profile profile) {
        if (profile.getUsername() != null) {
            return profile.getUsername();
        }
        return profile.getFullName();
    }
}
