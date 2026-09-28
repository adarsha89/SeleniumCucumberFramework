package com.scf.builder;

import com.scf.models.CheckoutInfo;

/** Builder pattern for {@link CheckoutInfo}, with sensible defaults for a "happy path" checkout. */
public final class CheckoutInfoBuilder {

    private String firstName = "Jane";
    private String lastName = "Doe";
    private String postalCode = "94107";

    public static CheckoutInfoBuilder aCheckoutInfo() {
        return new CheckoutInfoBuilder();
    }

    public CheckoutInfoBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public CheckoutInfoBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public CheckoutInfoBuilder withPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public CheckoutInfo build() {
        return new CheckoutInfo(firstName, lastName, postalCode);
    }
}
