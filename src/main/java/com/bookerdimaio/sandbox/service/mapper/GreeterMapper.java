package com.bookerdimaio.sandbox.service.mapper;

import com.bookerdimaio.sandbox.domain.*;
import com.bookerdimaio.sandbox.service.dto.GreeterDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Greeter} and its DTO {@link GreeterDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface GreeterMapper extends EntityMapper<GreeterDTO, Greeter> {

    default Greeter fromId(Long id) {
        if (id == null) {
            return null;
        }
        Greeter greeter = new Greeter();
        greeter.setId(id);
        return greeter;
    }

    default Greeter fromName(String firstName, String lastName) {
        if (firstName.isEmpty() || lastName.isEmpty()) {
            return null;
        }
        Greeter greeter = new Greeter();
        greeter.setFirstName(firstName);
        greeter.setLastName(lastName);
        return greeter;
    }
}
