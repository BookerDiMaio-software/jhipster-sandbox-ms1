package com.bookerdimaio.sandbox.service.impl;

import com.bookerdimaio.sandbox.service.GreeterService;
import com.bookerdimaio.sandbox.domain.Greeter;
import com.bookerdimaio.sandbox.repository.GreeterRepository;
import com.bookerdimaio.sandbox.service.dto.GreeterDTO;
import com.bookerdimaio.sandbox.service.mapper.GreeterMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Greeter}.
 */
@Service
@Transactional
public class GreeterServiceImpl implements GreeterService {

    private final Logger log = LoggerFactory.getLogger(GreeterServiceImpl.class);

    private final GreeterRepository greeterRepository;

    private final GreeterMapper greeterMapper;

    public GreeterServiceImpl(GreeterRepository greeterRepository, GreeterMapper greeterMapper) {
        this.greeterRepository = greeterRepository;
        this.greeterMapper = greeterMapper;
    }

    /**
     * Save a greeter.
     *
     * @param greeterDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public GreeterDTO save(GreeterDTO greeterDTO) {
        log.debug("Request to save Greeter : {}", greeterDTO);
        Greeter greeter = greeterMapper.toEntity(greeterDTO);
        greeter = greeterRepository.save(greeter);
        return greeterMapper.toDto(greeter);
    }

    /**
     * Get all the greeters.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<GreeterDTO> findAll() {
        log.debug("Request to get all Greeters");
        return greeterRepository.findAll().stream()
            .map(greeterMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one greeter by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<GreeterDTO> findOne(Long id) {
        log.debug("Request to get Greeter : {}", id);
        return greeterRepository.findById(id)
            .map(greeterMapper::toDto);
    }

    /**
     * Get one greeter by first name and last name.
     *
     * @param firstName the first name of the entity.
     * @param lastName the last name of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<GreeterDTO> findGreeter(String firstName, String lastName) {
        log.debug("Request to get Greeter: {0}, {1}", firstName, lastName);
        return greeterRepository.findGreeter(firstName, lastName)
            .map(greeterMapper::toDto);
    }

    /**
     * Get one greeter alphabetical last name (first).
     *
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<GreeterDTO> findFirstByOrderByLastNameAsc() {
        log.debug("Request to get firse alpha-last name  Greeter");
        return greeterRepository.findFirstByOrderByLastNameAsc()
            .map(greeterMapper::toDto);
    }

    /**
     * Delete the greeter by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Greeter : {}", id);
        greeterRepository.deleteById(id);
    }
}
