package com.hometown.common.security;

/** Role names used across HomeTown. Admin is a role, not a separate service. */
public final class Roles {

    public static final String CUSTOMER = "CUSTOMER";
    public static final String ADMIN = "ADMIN";

    /** Spring Security authority prefix form. */
    public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    private Roles() {
    }
}
