package com.nookx.api.service;

import com.nookx.api.domain.MegaAsset;
import com.nookx.api.repository.MegaAssetRepository;
import com.nookx.api.service.dto.MegaAssetDTO;
import com.nookx.api.service.mapper.MegaAssetMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link MegaAsset}.
 */
@Service
@Transactional
public class MegaAssetService {

    private static final Logger LOG = LoggerFactory.getLogger(MegaAssetService.class);

    private final MegaAssetRepository megaAssetRepository;

    private final MegaAssetMapper megaAssetMapper;

    public MegaAssetService(MegaAssetRepository megaAssetRepository, MegaAssetMapper megaAssetMapper) {
        this.megaAssetRepository = megaAssetRepository;
        this.megaAssetMapper = megaAssetMapper;
    }

    public MegaAssetDTO save(MegaAssetDTO megaAssetDTO) {
        LOG.debug("Request to save MegaAsset : {}", megaAssetDTO);
        MegaAsset megaAsset = megaAssetMapper.toEntity(megaAssetDTO);
        megaAsset = megaAssetRepository.save(megaAsset);
        return megaAssetMapper.toDto(megaAsset);
    }

    @Transactional(readOnly = true)
    public Optional<MegaAssetDTO> findOne(Long id) {
        LOG.debug("Request to get MegaAsset : {}", id);
        return megaAssetRepository.findById(id).map(megaAssetMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete MegaAsset : {}", id);
        megaAssetRepository.deleteById(id);
    }
}
