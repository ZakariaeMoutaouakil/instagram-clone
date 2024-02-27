package com.instagram.demo.controller.auth;

/**
 * Represents the credentials for registering a new user.
 * Includes fields for username, email, password, first name, and last name.
 */
public record RegisterUserCredentials(String username,
                                      String email,
                                      String password,
                                      String firstname,
                                      String lastname) {
}
