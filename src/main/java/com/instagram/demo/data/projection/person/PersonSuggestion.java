package com.instagram.demo.data.projection.person;

/**
 * This interface represents a projection of a person entity specifically used for suggesting users,
 * providing only the username and photo URL of the person.
 */
public interface PersonSuggestion {
    /**
     * Gets the username of the suggested person.
     *
     * @return The username of the suggested person.
     */
    String getUsername();

    /**
     * Gets the photo URL of the suggested person.
     *
     * @return The photo URL of the suggested person.
     */
    String getPhoto();
}
