package com.nookx.api.service.mapper;

import com.nookx.api.domain.MegaAsset;
import com.nookx.api.domain.Profile;
import com.nookx.api.domain.ProfileImage;
import com.nookx.api.service.dto.MegaAssetDTO;
import com.nookx.api.service.dto.ProfileDTO;
import com.nookx.api.service.dto.ProfileImageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProfileImage} and its DTO {@link ProfileImageDTO}.
 */
@Mapper(componentModel = "spring", uses = { MegaAssetMapper.class })
public interface ProfileImageMapper extends EntityMapper<ProfileImageDTO, ProfileImage> {
    @Mapping(target = "profile", source = "profile", qualifiedByName = "profileId")
    @Mapping(target = "asset", source = "asset", qualifiedByName = "megaAssetId")
    ProfileImageDTO toDto(ProfileImage entity);

    @Override
    @Mapping(target = "profile", source = "profile", qualifiedByName = "profileFromDtoId")
    @Mapping(target = "asset", source = "asset", qualifiedByName = "megaAssetFromDtoId")
    ProfileImage toEntity(ProfileImageDTO dto);

    @Override
    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "profile", source = "profile", qualifiedByName = "profileFromDtoId")
    @Mapping(target = "asset", source = "asset", qualifiedByName = "megaAssetFromDtoId")
    void partialUpdate(@MappingTarget ProfileImage entity, ProfileImageDTO dto);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);

    @Named("megaAssetId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MegaAssetDTO toDtoMegaAssetId(MegaAsset asset);

    @Named("profileFromDtoId")
    default Profile profileFromDtoId(ProfileDTO dto) {
        if (dto == null) {
            return null;
        }
        Profile profile = new Profile();
        profile.setId(dto.getId());
        return profile;
    }
}
