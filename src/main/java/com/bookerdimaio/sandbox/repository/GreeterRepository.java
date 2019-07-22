package com.bookerdimaio.sandbox.repository;

import java.util.List;
import java.util.Optional;

import com.bookerdimaio.sandbox.domain.Greeter;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the Greeter entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GreeterRepository extends JpaRepository<Greeter, Long> {

    @Query("SELECT g FROM Greeter g WHERE g.firstName = :firstName AND g.lastName = :lastName")
    Optional<Greeter> findGreeter(@Param("firstName") String firstName, @Param("lastName") String lastName);
}
