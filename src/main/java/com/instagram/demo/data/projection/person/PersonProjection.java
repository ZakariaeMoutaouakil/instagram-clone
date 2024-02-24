package com.instagram.demo.data.projection.person;

/**
 * This interface represents a projection of a person entity, providing selected attributes of a person.
 */
public interface PersonProjection {
    /**
     * Gets the username of the person.
     *
     * @return The username of the person.
     */
    String getUsername();

    /**
     * Gets the bio of the person.
     *
     * @return The bio of the person.
     */
    String getBio();

    /**
     * Gets the firstname of the person.
     *
     * @return The firstname of the person.
     */
    String getFirstname();

    /**
     * Gets the lastname of the person.
     *
     * @return The lastname of the person.
     */
    String getLastname();

    /**
     * Gets the validation status of the person.
     *
     * @return {@code true} if the person is validated, {@code false} otherwise.
     */
    Boolean getValidated();

    /**
     * Gets the photo URL of the person.
     *
     * @return The photo URL of the person.
     */
    String getPhoto();
}
