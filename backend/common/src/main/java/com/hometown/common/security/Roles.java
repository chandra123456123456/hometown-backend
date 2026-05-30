package com.hometown.common.security;

/** Role names used across HomeTown. Admin is a role, not a separate service. */
public final class Roles {

    public static final String CUSTOMER = "CUSTOMER";
    public static final String ADMIN = "ADMIN";
    public static final String DEVELOPER = "DEVELOPER";

    /** Spring Security authority prefix form. */
    public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_DEVELOPER = "ROLE_DEVELOPER";

    private Roles() {
    }
}
