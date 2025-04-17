package com.leverx.RatingSystemRest.Presentation.ConstMessages;

/**
 * This class contains constant messages related to comment operations.
 * These messages are used throughout the application to standardize and centralize the text
 * used for comments approval, deletion, and other comment-related activities.
 * This is a utility class and should not be instantiated.
 */
public final class CommentConstMessages {

  /**
   * Message indicating that a comment has been approved.
   */
  public static final String COMMENT_APPROVED = "Comment approved";

  /**
   * Message indicating that a comment has been deleted.
   */
  public static final String COMMENT_DELETED_MESSAGE = "Comment Deleted";

  // Private constructor to prevent instantiation
  private CommentConstMessages() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
}

