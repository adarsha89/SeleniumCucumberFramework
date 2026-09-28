package com.scf.builder;

import com.scf.config.ConfigManager;
import com.scf.models.User;

/**
 * Builder pattern for {@link User}.
 * <p>
 * A plain constructor would be fine for two fields, but test data classes
 * tend to grow (email, first/last name, role, locale...), and a fluent
 * builder keeps call sites readable ("build a locked-out user", "build the
 * env-configured test user") without an explosion of constructor overloads.
 * It also gives us named factory methods for the fixed SauceDemo accounts
 * without repeating the password string everywhere.
 */
public final class UserBuilder {

    private static final String STANDARD_PASSWORD = "secret_sauce";

    private String username = "";
    private String password = "";

    public static UserBuilder aUser() {
        return new UserBuilder();
    }

    public UserBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public User build() {
        return new User(username, password);
    }

    // --- Named presets -----------------------------------------------------

    public static User standardUser() {
        return aUser().withUsername("standard_user").withPassword(STANDARD_PASSWORD).build();
    }

    public static User lockedOutUser() {
        return aUser().withUsername("locked_out_user").withPassword(STANDARD_PASSWORD).build();
    }

    public static User problemUser() {
        return aUser().withUsername("problem_user").withPassword(STANDARD_PASSWORD).build();
    }

    public static User performanceGlitchUser() {
        return aUser().withUsername("performance_glitch_user").withPassword(STANDARD_PASSWORD).build();
    }

    /**
     * Uses TEST_USER_EMAIL / TEST_USER_PASSWORD from configuration. SauceDemo
     * only accepts its fixed usernames, so this is intended for negative
     * "invalid credentials" scenarios that demonstrate env-driven test data.
     */
    public static User envConfiguredUser() {
        ConfigManager config = ConfigManager.get();
        return aUser().withUsername(config.testUserEmail()).withPassword(config.testUserPassword()).build();
    }
}
