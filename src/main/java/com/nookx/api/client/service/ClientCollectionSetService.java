package com.nookx.api.client.service;

import com.nookx.api.client.dto.ClientCollectionSetDTO;
import com.nookx.api.client.dto.ClientSetDTO;
import com.nookx.api.client.dto.ClientSetLiteDTO;
import com.nookx.api.domain.MegaSet;
import com.nookx.api.domain.Profile;
import com.nookx.api.domain.ProfileCollection;
import com.nookx.api.domain.ProfileCollectionSet;
import com.nookx.api.domain.User;
import com.nookx.api.domain.enumeration.ProfileCollectionSetStatus;
import com.nookx.api.repository.MegaSetRepository;
import com.nookx.api.repository.ProfileCollectionRepository;
import com.nookx.api.repository.ProfileCollectionSetRepository;
import com.nookx.api.service.ProfileService;
import com.nookx.api.service.UserService;
import com.nookx.api.service.dto.ProfileCollectionSetDTO;
import com.nookx.api.service.mapper.ProfileCollectionSetMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Client-facing service that manages the {@link ProfileCollectionSet} relationship
 * between a {@link ProfileCollection} owned by the current profile and the
 * {@link MegaSet}s that belong (or want to be added) to it.
 *
 * <p>All public methods enforce that the targeted collection belongs to the current
 * profile, returning {@code 404 Not Found} otherwise. Add operations additionally
 * require each set to be either {@code publicItem=true} or owned by the current user.</p>
 */
@Slf4j
@Service
@Transactional
public class ClientCollectionSetService {

    private final ProfileService profileService;
    private final UserService userService;
    private final ProfileCollectionRepository profileCollectionRepository;
    private final ProfileCollectionSetRepository profileCollectionSetRepository;
    private final MegaSetRepository megaSetRepository;
    private final ClientSetService clientSetService;
    private final ProfileCollectionSetMapper profileCollectionSetMapper;

    public ClientCollectionSetService(
        ProfileService profileService,
        UserService userService,
        ProfileCollectionRepository profileCollectionRepository,
        ProfileCollectionSetRepository profileCollectionSetRepository,
        MegaSetRepository megaSetRepository,
        ClientSetService clientSetService,
        ProfileCollectionSetMapper profileCollectionSetMapper
    ) {
        this.profileService = profileService;
        this.userService = userService;
        this.profileCollectionRepository = profileCollectionRepository;
        this.profileCollectionSetRepository = profileCollectionSetRepository;
        this.megaSetRepository = megaSetRepository;
        this.clientSetService = clientSetService;
        this.profileCollectionSetMapper = profileCollectionSetMapper;
    }

    /**
     * Add every set in {@code requested} to {@code collectionId}.
     *
     * <p>The operation is idempotent: sets already present in the collection are
     * silently skipped. The {@code owned} flag and the sell-listing fields
     * ({@code userNotes}, {@code price}, {@code quantityToSell}, {@code status})
     * from the request are persisted as provided; {@code dateAdded} is always set
     * to today and {@code status} defaults to {@link ProfileCollectionSetStatus#AVAILABLE}
     * when omitted.</p>
     *
     * @throws ResponseStatusException 404 if the collection does not belong to the
     *         current profile, or if any of the requested sets is neither public
     *         nor owned by the current user.
     */
    public void addSetsToCollection(Long collectionId, List<ClientCollectionSetDTO> requested) {
        log.debug("Request to add {} sets to ClientCollection : {}", requested == null ? 0 : requested.size(), collectionId);
        ProfileCollection collection = getOwnedCollectionOrNotFound(collectionId);
        if (requested == null || requested.isEmpty()) {
            return;
        }
        Long currentUserId = getCurrentUserIdOrNotFound();

        for (ClientCollectionSetDTO dto : requested) {
            if (dto == null || dto.getSetId() == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            if (profileCollectionSetRepository.existsByCollection_IdAndSet_Id(collectionId, dto.getSetId())) {
                continue;
            }
            MegaSet megaSet = megaSetRepository
                .findById(dto.getSetId())
                .filter(set -> isAddableByCurrentUser(set, currentUserId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            ProfileCollectionSet entity = new ProfileCollectionSet()
                .collection(collection)
                .set(megaSet)
                .owned(dto.getOwned())
                .userNotes(dto.getUserNotes())
                .price(dto.getPrice())
                .quantityToSell(dto.getQuantityToSell())
                .status(dto.getStatus() != null ? dto.getStatus() : ProfileCollectionSetStatus.AVAILABLE)
                .dateAdded(LocalDate.now());
            profileCollectionSetRepository.save(entity);
        }
    }

    /**
     * Remove the {@link ProfileCollectionSet} that links {@code setId} (a
     * {@link MegaSet} id) to {@code collectionId}.
     *
     * @throws ResponseStatusException 404 if the collection does not belong to the
     *         current profile, or no such link exists.
     */
    public void removeSetFromCollection(Long collectionId, Long setId) {
        log.debug("Request to remove set {} from ClientCollection : {}", setId, collectionId);
        getOwnedCollectionOrNotFound(collectionId);
        ProfileCollectionSet link = profileCollectionSetRepository
            .findByCollection_IdAndSet_Id(collectionId, setId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        profileCollectionSetRepository.delete(link);
    }

    /**
     * Update the editable details of the {@link ProfileCollectionSet} that links
     * {@code setId} (a {@link MegaSet} id) to {@code collectionId}.
     *
     * <p>PUT semantics: the editable fields ({@code owned}, {@code userNotes},
     * {@code price}, {@code quantityToSell}, {@code status}) are overwritten with
     * the values from {@code dto} (including {@code null}). The {@code id},
     * {@code dateAdded}, {@code collection} and {@code set} fields are ignored.</p>
     *
     * @throws ResponseStatusException 404 if the collection does not belong to the
     *         current profile, or no such link exists.
     */
    public ProfileCollectionSetDTO updateSetDetails(Long collectionId, Long setId, ProfileCollectionSetDTO dto) {
        log.debug("Request to update details of set {} in ClientCollection {} : {}", setId, collectionId, dto);
        getOwnedCollectionOrNotFound(collectionId);
        ProfileCollectionSet link = profileCollectionSetRepository
            .findByCollection_IdAndSet_Id(collectionId, setId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        link.setOwned(dto.getOwned());
        link.setUserNotes(dto.getUserNotes());
        link.setPrice(dto.getPrice());
        link.setQuantityToSell(dto.getQuantityToSell());
        link.setStatus(dto.getStatus());

        ProfileCollectionSet saved = profileCollectionSetRepository.save(link);
        return profileCollectionSetMapper.toDto(saved);
    }

    /**
     * Get every {@link MegaSet} contained in {@code collectionId}, fully enriched
     * as a {@link ClientSetDTO}.
     *
     * @throws ResponseStatusException 404 if the collection does not belong to the
     *         current profile.
     */
    @Transactional(readOnly = true)
    public List<ClientSetLiteDTO> getSetsByCollection(Long collectionId) {
        log.debug("Request to get sets for ClientCollection : {}", collectionId);
        getOwnedCollectionOrNotFound(collectionId);
        return clientSetService.getSetsByCollectionId(collectionId);
    }

    private ProfileCollection getOwnedCollectionOrNotFound(Long collectionId) {
        Profile currentProfile = profileService.getCurrentProfile();
        if (currentProfile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return profileCollectionRepository
            .findById(collectionId)
            .filter(
                collection -> collection.getProfile() != null && Objects.equals(collection.getProfile().getId(), currentProfile.getId())
            )
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private Long getCurrentUserIdOrNotFound() {
        return userService
            .getUserWithAuthorities()
            .map(User::getId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private boolean isAddableByCurrentUser(MegaSet set, Long currentUserId) {
        if (set.isPublicItem()) {
            return true;
        }
        return Optional.ofNullable(set.getOwner())
            .map(User::getId)
            .map(ownerId -> Objects.equals(ownerId, currentUserId))
            .orElse(false);
    }
}
