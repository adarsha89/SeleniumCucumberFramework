package com.scf.models;

/**
 * Immutable value object representing login credentials. Constructed only
 * via {@code UserBuilder} - see that class for why.
 */
public final class User {

    private final String username;
    private final String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "User{username='" + username + "'}"; // never print passwords
    }
}
