package com.nookx.api.service;

import com.nookx.api.client.dto.ClientInterestDTO;
import com.nookx.api.domain.Interest;
import com.nookx.api.domain.Profile;
import com.nookx.api.domain.ProfileInterest;
import com.nookx.api.repository.InterestRepository;
import com.nookx.api.repository.ProfileInterestRepository;
import com.nookx.api.service.mapper.InterestMapper;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Interest}.
 */
@Service
@Transactional
public class InterestService {

    private static final Logger LOG = LoggerFactory.getLogger(InterestService.class);

    private final InterestRepository interestRepository;

    private final InterestMapper interestMapper;

    private final ProfileService profileService;

    private final ProfileInterestRepository profileInterestRepository;

    public InterestService(
        InterestRepository interestRepository,
        InterestMapper interestMapper,
        ProfileService profileService,
        ProfileInterestRepository profileInterestRepository
    ) {
        this.interestRepository = interestRepository;
        this.interestMapper = interestMapper;
        this.profileService = profileService;
        this.profileInterestRepository = profileInterestRepository;
    }

    @Transactional
    public ClientInterestDTO save(ClientInterestDTO interestDTO) {
        LOG.debug("Request to save Interest : {}", interestDTO);
        Interest interest = interestMapper.toEntity(interestDTO);
        interest.setSystem(false);
        interest.setPublic(interestDTO.isPublic());
        interest.setDeleted(false);
        interest.setDeletedDate(null);
        interest = interestRepository.save(interest);

        Profile currentProfile = profileService.getCurrentProfile();
        subscribeToInterest(interest, currentProfile);

        return interestMapper.toDto(interest);
    }

    private void subscribeToInterest(Interest interest, Profile currentProfile) {
        ProfileInterest profileInterest = new ProfileInterest();
        profileInterest.setInterest(interest);
        profileInterest.setProfile(currentProfile);
        profileInterestRepository.save(profileInterest);
    }

    /**
     * Synchronizes the current profile's interests with the provided list:
     * the incoming list represents the complete desired state, so any
     * currently linked interest not present is removed and any new one is added.
     */
    public void subscribeCurrentProfileToInterests(List<Long> interestIds) {
        LOG.debug("Request to sync current profile interests : {}", interestIds);
        Profile currentProfile = profileService.getCurrentProfile();
        Long profileId = currentProfile.getId();

        Set<Long> desiredIds = interestIds == null ? new HashSet<>() : new HashSet<>(interestIds);
        Set<Long> currentIds = new HashSet<>(profileInterestRepository.findInterestIdsByProfileId(profileId));

        Set<Long> toRemove = new HashSet<>(currentIds);
        toRemove.removeAll(desiredIds);
        if (!toRemove.isEmpty()) {
            profileInterestRepository.deleteByProfile_IdAndInterest_IdIn(profileId, toRemove);
        }

        Set<Long> toAdd = new HashSet<>(desiredIds);
        toAdd.removeAll(currentIds);
        for (Long interestId : toAdd) {
            Interest interest = interestRepository
                .findByIdAndDeletedFalse(interestId)
                .orElseThrow(() -> new BadRequestAlertException("Interest not found", "interest", "idnotfound"));
            subscribeToInterest(interest, currentProfile);
        }
    }

    public ClientInterestDTO update(ClientInterestDTO interestDTO) {
        LOG.debug("Request to update Interest : {}", interestDTO);

        Interest existing = interestRepository
            .findByIdAndDeletedFalse(interestDTO.getId())
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", "interest", "idnotfound"));

        assertInterestUpdatableByCurrentProfile(existing);
        assertCurrentUserIsOwner(existing);

        existing.setName(interestDTO.getName());
        existing.setDescription(interestDTO.getDescription());
        existing.setPublic(interestDTO.isPublic());

        existing = interestRepository.save(existing);
        return interestMapper.toDto(existing);
    }

    private void assertInterestUpdatableByCurrentProfile(Interest interest) {
        if (interest.isSystem()) {
            throw new BadRequestAlertException("System interests cannot be updated", "interest", "systeminterest");
        }
    }

    private void assertCurrentUserIsOwner(Interest interest) {
        Profile currentProfile = profileService.getCurrentProfile();
        if (!profileInterestRepository.existsByProfile_IdAndInterest_Id(currentProfile.getId(), interest.getId())) {
            throw new AccessDeniedException("Current profile is not linked to this interest");
        }
    }

    @Transactional(readOnly = true)
    public List<ClientInterestDTO> findAll(boolean isSystem, boolean isPublic) {
        LOG.debug("Request to get Interests for current profile and system catalog");
        Long profileId = profileService.getCurrentProfile().getId();
        Set<Long> subscribedIds = new HashSet<>(profileInterestRepository.findInterestIdsByProfileId(profileId));
        return interestRepository
            .findAllLinkedToProfileOrSystem(profileId)
            .stream()
            .filter(i -> i.isSystem() == isSystem && i.isPublic() == isPublic)
            .map(interest -> toDtoWithSubscription(interest, subscribedIds))
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Transactional(readOnly = true)
    public List<ClientInterestDTO> findAllForCurrentProfile() {
        LOG.debug("Request to get Interests linked to current profile only");
        Long profileId = profileService.getCurrentProfile().getId();
        return interestRepository
            .findAllLinkedToProfile(profileId)
            .stream()
            .map(interestMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Transactional(readOnly = true)
    public Optional<ClientInterestDTO> findOne(Long id) {
        LOG.debug("Request to get Interest : {} for current profile", id);
        Long profileId = profileService.getCurrentProfile().getId();
        Set<Long> subscribedIds = new HashSet<>(profileInterestRepository.findInterestIdsByProfileId(profileId));
        return interestRepository
            .findByIdLinkedToProfileOrSystem(id, profileId)
            .map(interest -> toDtoWithSubscription(interest, subscribedIds));
    }

    private ClientInterestDTO toDtoWithSubscription(Interest interest, Set<Long> subscribedInterestIds) {
        ClientInterestDTO dto = interestMapper.toDto(interest);
        dto.setSubscribed(subscribedInterestIds.contains(interest.getId()));
        return dto;
    }

    public void delete(Long id) {
        LOG.debug("Request to delete Interest : {}", id);
        Interest existing = interestRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", "interest", "idnotfound"));

        if (existing.isSystem()) throw new AccessDeniedException("Current profile is not linked to this interest");
        assertCurrentUserIsOwner(existing);
        existing.setDeleted(true);
        existing.setDeletedDate(Instant.now());
        interestRepository.save(existing);
    }
}
