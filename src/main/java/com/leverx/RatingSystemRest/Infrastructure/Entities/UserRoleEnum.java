package com.leverx.RatingSystemRest.Infrastructure.Entities;

/**
 * Enumeration representing roles a user can have in the system.
 */
public enum UserRoleEnum {

  /**
   * Admin role with elevated privileges for managing the system.
   */
  ADMIN,

  /**
   * Seller role with permissions to manage their own games and interact with reviews.
   */
  SELLER
}
