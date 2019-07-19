package com.bookerdimaio.sandbox.service;

import com.bookerdimaio.sandbox.service.dto.GreeterDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.bookerdimaio.sandbox.domain.Greeter}.
 */
public interface GreeterService {

    /**
     * Save a greeter.
     *
     * @param greeterDTO the entity to save.
     * @return the persisted entity.
     */
    GreeterDTO save(GreeterDTO greeterDTO);

    /**
     * Get all the greeters.
     *
     * @return the list of entities.
     */
    List<GreeterDTO> findAll();


    /**
     * Get the "id" greeter.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<GreeterDTO> findOne(Long id);

    /**
     * Delete the "id" greeter.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
