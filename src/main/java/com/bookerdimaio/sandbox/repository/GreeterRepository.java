package com.bookerdimaio.sandbox.repository;

import com.bookerdimaio.sandbox.domain.Greeter;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Greeter entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GreeterRepository extends JpaRepository<Greeter, Long> {

}
