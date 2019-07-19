package com.bookerdimaio.sandbox.web.rest;

import com.bookerdimaio.sandbox.service.GreeterService;
import com.bookerdimaio.sandbox.web.rest.errors.BadRequestAlertException;
import com.bookerdimaio.sandbox.service.dto.GreeterDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.bookerdimaio.sandbox.domain.Greeter}.
 */
@RestController
@RequestMapping("/api")
public class GreeterResource {

    private final Logger log = LoggerFactory.getLogger(GreeterResource.class);

    private static final String ENTITY_NAME = "microservice1Greeter";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GreeterService greeterService;

    public GreeterResource(GreeterService greeterService) {
        this.greeterService = greeterService;
    }

    /**
     * {@code POST  /greeters} : Create a new greeter.
     *
     * @param greeterDTO the greeterDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new greeterDTO, or with status {@code 400 (Bad Request)} if the greeter has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/greeters")
    public ResponseEntity<GreeterDTO> createGreeter(@Valid @RequestBody GreeterDTO greeterDTO) throws URISyntaxException {
        log.debug("REST request to save Greeter : {}", greeterDTO);
        if (greeterDTO.getId() != null) {
            throw new BadRequestAlertException("A new greeter cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GreeterDTO result = greeterService.save(greeterDTO);
        return ResponseEntity.created(new URI("/api/greeters/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /greeters} : Updates an existing greeter.
     *
     * @param greeterDTO the greeterDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated greeterDTO,
     * or with status {@code 400 (Bad Request)} if the greeterDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the greeterDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/greeters")
    public ResponseEntity<GreeterDTO> updateGreeter(@Valid @RequestBody GreeterDTO greeterDTO) throws URISyntaxException {
        log.debug("REST request to update Greeter : {}", greeterDTO);
        if (greeterDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        GreeterDTO result = greeterService.save(greeterDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, greeterDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /greeters} : get all the greeters.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of greeters in body.
     */
    @GetMapping("/greeters")
    public List<GreeterDTO> getAllGreeters() {
        log.debug("REST request to get all Greeters");
        return greeterService.findAll();
    }

    /**
     * {@code GET  /greeters/:id} : get the "id" greeter.
     *
     * @param id the id of the greeterDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the greeterDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/greeters/{id}")
    public ResponseEntity<GreeterDTO> getGreeter(@PathVariable Long id) {
        log.debug("REST request to get Greeter : {}", id);
        Optional<GreeterDTO> greeterDTO = greeterService.findOne(id);
        return ResponseUtil.wrapOrNotFound(greeterDTO);
    }

    /**
     * {@code DELETE  /greeters/:id} : delete the "id" greeter.
     *
     * @param id the id of the greeterDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/greeters/{id}")
    public ResponseEntity<Void> deleteGreeter(@PathVariable Long id) {
        log.debug("REST request to delete Greeter : {}", id);
        greeterService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
