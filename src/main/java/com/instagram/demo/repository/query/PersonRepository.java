package com.instagram.demo.repository.query;

import com.instagram.demo.repository.schema.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PersonRepository extends CrudRepository<Person, Long> {
    Optional<PersonProjection> findByUsername(String username);

}
