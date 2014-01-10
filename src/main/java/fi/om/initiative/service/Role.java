package fi.om.initiative.service;

public enum Role {
/**
 * Authenticated - logged in but not persisted as user.
 */
AUTHENTICATED, 
/**
 * Registered - logged in and persisted as user.
 */
REGISTERED, 
/**
 * VRK official (registered)
 */
VRK, 
/**
 * OM official (registered)
 */
OM
}
