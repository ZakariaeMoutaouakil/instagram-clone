package com.instagram.demo.data.projection.person;

/**
 * This interface represents a projection of a person entity specifically used for feed purposes,
 * providing only the ID of the person.
 */
public interface PersonFeed {
    /**
     * Gets the ID of the person.
     *
     * @return The ID of the person.
     */
    Long getId();
}
