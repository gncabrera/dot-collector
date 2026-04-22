package com.nookx.api.service.mapper;

import com.nookx.api.client.dto.ClientInterestDTO;
import com.nookx.api.domain.Interest;
import com.nookx.api.domain.Profile;
import com.nookx.api.domain.ProfileCollection;
import com.nookx.api.service.dto.ProfileCollectionDTO;
import com.nookx.api.service.dto.ProfileDTO;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProfileCollection} and its DTO {@link ProfileCollectionDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProfileCollectionImageMapper.class, InterestMapper.class })
public interface ProfileCollectionMapper extends EntityMapper<ProfileCollectionDTO, ProfileCollection> {
    @Mapping(target = "profile", source = "profile", qualifiedByName = "profileId")
    @Mapping(target = "interests", source = "interests", qualifiedByName = "interestIdSet")
    @Mapping(target = "image", source = "image")
    ProfileCollectionDTO toDto(ProfileCollection s);

    @Override
    @Mapping(target = "image.profileCollection", ignore = true)
    @Mapping(target = "interests", source = "interests", qualifiedByName = "toEntityInterestIdSet")
    @Mapping(target = "removeInterest", ignore = true)
    ProfileCollection toEntity(ProfileCollectionDTO dto);

    @Override
    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "image.profileCollection", ignore = true)
    @Mapping(target = "interests", source = "interests", qualifiedByName = "toEntityInterestIdSet")
    @Mapping(target = "removeInterest", ignore = true)
    void partialUpdate(@MappingTarget ProfileCollection entity, ProfileCollectionDTO dto);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);

    @Named("interestIdSet")
    default Set<ClientInterestDTO> toDtoInterestIdSet(Set<Interest> interests) {
        if (interests == null) {
            return new HashSet<>();
        }
        return interests
            .stream()
            .map(interest -> {
                ClientInterestDTO dto = new ClientInterestDTO();
                dto.setId(interest.getId());
                return dto;
            })
            .collect(Collectors.toSet());
    }

    @Named("toEntityInterestIdSet")
    default Set<Interest> toEntityInterestIdSet(Set<ClientInterestDTO> dtos) {
        if (dtos == null) {
            return new HashSet<>();
        }
        return dtos
            .stream()
            .map(dto -> {
                Interest interest = new Interest();
                interest.setId(dto.getId());
                return interest;
            })
            .collect(Collectors.toSet());
    }
}
