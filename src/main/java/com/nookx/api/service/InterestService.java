package com.nookx.api.service;

import com.nookx.api.domain.Interest;
import com.nookx.api.repository.InterestRepository;
import com.nookx.api.service.dto.InterestDTO;
import com.nookx.api.service.mapper.InterestMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public InterestService(InterestRepository interestRepository, InterestMapper interestMapper) {
        this.interestRepository = interestRepository;
        this.interestMapper = interestMapper;
    }

    public InterestDTO save(InterestDTO interestDTO) {
        LOG.debug("Request to save Interest : {}", interestDTO);
        Interest interest = interestMapper.toEntity(interestDTO);
        interest = interestRepository.save(interest);
        return interestMapper.toDto(interest);
    }

    public InterestDTO update(InterestDTO interestDTO) {
        LOG.debug("Request to update Interest : {}", interestDTO);
        Interest interest = interestMapper.toEntity(interestDTO);
        interest = interestRepository.save(interest);
        return interestMapper.toDto(interest);
    }

    public Optional<InterestDTO> partialUpdate(InterestDTO interestDTO) {
        LOG.debug("Request to partially update Interest : {}", interestDTO);

        return interestRepository
            .findById(interestDTO.getId())
            .map(existingInterest -> {
                interestMapper.partialUpdate(existingInterest, interestDTO);

                return existingInterest;
            })
            .map(interestRepository::save)
            .map(interestMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<InterestDTO> findAll() {
        LOG.debug("Request to get all Interests");
        return interestRepository.findAll().stream().map(interestMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Transactional(readOnly = true)
    public Optional<InterestDTO> findOne(Long id) {
        LOG.debug("Request to get Interest : {}", id);
        return interestRepository.findById(id).map(interestMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete Interest : {}", id);
        interestRepository.deleteById(id);
    }
}
