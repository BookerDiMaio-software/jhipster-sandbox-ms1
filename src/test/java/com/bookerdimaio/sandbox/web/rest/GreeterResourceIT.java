package com.bookerdimaio.sandbox.web.rest;

import com.bookerdimaio.sandbox.Microservice1App;
import com.bookerdimaio.sandbox.domain.Greeter;
import com.bookerdimaio.sandbox.repository.GreeterRepository;
import com.bookerdimaio.sandbox.service.GreeterService;
import com.bookerdimaio.sandbox.service.dto.GreeterDTO;
import com.bookerdimaio.sandbox.service.mapper.GreeterMapper;
import com.bookerdimaio.sandbox.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.bookerdimaio.sandbox.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link GreeterResource} REST controller.
 */
@SpringBootTest(classes = Microservice1App.class)
public class GreeterResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SALUTATION = "AAAAAAAAAA";
    private static final String UPDATED_SALUTATION = "BBBBBBBBBB";

    @Autowired
    private GreeterRepository greeterRepository;

    @Autowired
    private GreeterMapper greeterMapper;

    @Autowired
    private GreeterService greeterService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restGreeterMockMvc;

    private Greeter greeter;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final GreeterResource greeterResource = new GreeterResource(greeterService);
        this.restGreeterMockMvc = MockMvcBuilders.standaloneSetup(greeterResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Greeter createEntity(EntityManager em) {
        Greeter greeter = new Greeter()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .salutation(DEFAULT_SALUTATION);
        return greeter;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Greeter createUpdatedEntity(EntityManager em) {
        Greeter greeter = new Greeter()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .salutation(UPDATED_SALUTATION);
        return greeter;
    }

    @BeforeEach
    public void initTest() {
        greeter = createEntity(em);
    }

    @Test
    @Transactional
    public void createGreeter() throws Exception {
        int databaseSizeBeforeCreate = greeterRepository.findAll().size();

        // Create the Greeter
        GreeterDTO greeterDTO = greeterMapper.toDto(greeter);
        restGreeterMockMvc.perform(post("/api/greeters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(greeterDTO)))
            .andExpect(status().isCreated());

        // Validate the Greeter in the database
        List<Greeter> greeterList = greeterRepository.findAll();
        assertThat(greeterList).hasSize(databaseSizeBeforeCreate + 1);
        Greeter testGreeter = greeterList.get(greeterList.size() - 1);
        assertThat(testGreeter.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testGreeter.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testGreeter.getSalutation()).isEqualTo(DEFAULT_SALUTATION);
    }

    @Test
    @Transactional
    public void createGreeterWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = greeterRepository.findAll().size();

        // Create the Greeter with an existing ID
        greeter.setId(1L);
        GreeterDTO greeterDTO = greeterMapper.toDto(greeter);

        // An entity with an existing ID cannot be created, so this API call must fail
        restGreeterMockMvc.perform(post("/api/greeters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(greeterDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Greeter in the database
        List<Greeter> greeterList = greeterRepository.findAll();
        assertThat(greeterList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = greeterRepository.findAll().size();
        // set the field null
        greeter.setFirstName(null);

        // Create the Greeter, which fails.
        GreeterDTO greeterDTO = greeterMapper.toDto(greeter);

        restGreeterMockMvc.perform(post("/api/greeters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(greeterDTO)))
            .andExpect(status().isBadRequest());

        List<Greeter> greeterList = greeterRepository.findAll();
        assertThat(greeterList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = greeterRepository.findAll().size();
        // set the field null
        greeter.setLastName(null);

        // Create the Greeter, which fails.
        GreeterDTO greeterDTO = greeterMapper.toDto(greeter);

        restGreeterMockMvc.perform(post("/api/greeters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(greeterDTO)))
            .andExpect(status().isBadRequest());

        List<Greeter> greeterList = greeterRepository.findAll();
        assertThat(greeterList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSalutationIsRequired() throws Exception {
        int databaseSizeBeforeTest = greeterRepository.findAll().size();
        // set the field null
        greeter.setSalutation(null);

        // Create the Greeter, which fails.
        GreeterDTO greeterDTO = greeterMapper.toDto(greeter);

        restGreeterMockMvc.perform(post("/api/greeters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(greeterDTO)))
            .andExpect(status().isBadRequest());

        List<Greeter> greeterList = greeterRepository.findAll();
        assertThat(greeterList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllGreeters() throws Exception {
        // Initialize the database
        greeterRepository.saveAndFlush(greeter);

        // Get all the greeterList
        restGreeterMockMvc.perform(get("/api/greeters?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(greeter.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME.toString())))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME.toString())))
            .andExpect(jsonPath("$.[*].salutation").value(hasItem(DEFAULT_SALUTATION.toString())));
    }
    
    @Test
    @Transactional
    public void getGreeter() throws Exception {
        // Initialize the database
        greeterRepository.saveAndFlush(greeter);

        // Get the greeter
        restGreeterMockMvc.perform(get("/api/greeters/{id}", greeter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(greeter.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME.toString()))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME.toString()))
            .andExpect(jsonPath("$.salutation").value(DEFAULT_SALUTATION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingGreeter() throws Exception {
        // Get the greeter
        restGreeterMockMvc.perform(get("/api/greeters/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGreeter() throws Exception {
        // Initialize the database
        greeterRepository.saveAndFlush(greeter);

        int databaseSizeBeforeUpdate = greeterRepository.findAll().size();

        // Update the greeter
        Greeter updatedGreeter = greeterRepository.findById(greeter.getId()).get();
        // Disconnect from session so that the updates on updatedGreeter are not directly saved in db
        em.detach(updatedGreeter);
        updatedGreeter
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .salutation(UPDATED_SALUTATION);
        GreeterDTO greeterDTO = greeterMapper.toDto(updatedGreeter);

        restGreeterMockMvc.perform(put("/api/greeters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(greeterDTO)))
            .andExpect(status().isOk());

        // Validate the Greeter in the database
        List<Greeter> greeterList = greeterRepository.findAll();
        assertThat(greeterList).hasSize(databaseSizeBeforeUpdate);
        Greeter testGreeter = greeterList.get(greeterList.size() - 1);
        assertThat(testGreeter.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testGreeter.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testGreeter.getSalutation()).isEqualTo(UPDATED_SALUTATION);
    }

    @Test
    @Transactional
    public void updateNonExistingGreeter() throws Exception {
        int databaseSizeBeforeUpdate = greeterRepository.findAll().size();

        // Create the Greeter
        GreeterDTO greeterDTO = greeterMapper.toDto(greeter);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGreeterMockMvc.perform(put("/api/greeters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(greeterDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Greeter in the database
        List<Greeter> greeterList = greeterRepository.findAll();
        assertThat(greeterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteGreeter() throws Exception {
        // Initialize the database
        greeterRepository.saveAndFlush(greeter);

        int databaseSizeBeforeDelete = greeterRepository.findAll().size();

        // Delete the greeter
        restGreeterMockMvc.perform(delete("/api/greeters/{id}", greeter.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Greeter> greeterList = greeterRepository.findAll();
        assertThat(greeterList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Greeter.class);
        Greeter greeter1 = new Greeter();
        greeter1.setId(1L);
        Greeter greeter2 = new Greeter();
        greeter2.setId(greeter1.getId());
        assertThat(greeter1).isEqualTo(greeter2);
        greeter2.setId(2L);
        assertThat(greeter1).isNotEqualTo(greeter2);
        greeter1.setId(null);
        assertThat(greeter1).isNotEqualTo(greeter2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(GreeterDTO.class);
        GreeterDTO greeterDTO1 = new GreeterDTO();
        greeterDTO1.setId(1L);
        GreeterDTO greeterDTO2 = new GreeterDTO();
        assertThat(greeterDTO1).isNotEqualTo(greeterDTO2);
        greeterDTO2.setId(greeterDTO1.getId());
        assertThat(greeterDTO1).isEqualTo(greeterDTO2);
        greeterDTO2.setId(2L);
        assertThat(greeterDTO1).isNotEqualTo(greeterDTO2);
        greeterDTO1.setId(null);
        assertThat(greeterDTO1).isNotEqualTo(greeterDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(greeterMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(greeterMapper.fromId(null)).isNull();
    }
}
