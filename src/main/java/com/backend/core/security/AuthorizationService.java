package com.backend.core.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Service providing authorization checks for use with @PreAuthorize annotations.
 *
 * Example usage:
 * @PreAuthorize("@authorizationService.hasAccessToResource(#resourceId)")
 * public void someMethod(Long resourceId) { ... }
 */
@Service
@Slf4j
public class AuthorizationService {

    /**
     * Gets the currently authenticated user ID.
     *
     * @return the user ID, or null if not authenticated
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }

        return null;
    }

    /**
     * Checks if the current user is authenticated.
     *
     * @return true if user is authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        return getCurrentUserId() != null;
    }

    /**
     * Checks if the current user has access to a specific resource owned by a user.
     *
     * @param resourceOwnerId the ID of the resource owner
     * @return true if current user owns the resource, false otherwise
     */
    public boolean hasAccessToResource(Long resourceOwnerId) {
        Long currentUserId = getCurrentUserId();

        if (currentUserId == null || resourceOwnerId == null) {
            log.debug("Access denied: currentUserId={}, resourceOwnerId={}", currentUserId, resourceOwnerId);
            return false;
        }

        boolean hasAccess = Objects.equals(currentUserId, resourceOwnerId);
        log.debug("Access check: currentUserId={}, resourceOwnerId={}, hasAccess={}",
                  currentUserId, resourceOwnerId, hasAccess);

        return hasAccess;
    }

    /**
     * Checks if the current user is the owner of a resource.
     * Alias for hasAccessToResource for better readability.
     *
     * @param ownerId the ID of the resource owner
     * @return true if current user is the owner, false otherwise
     */
    public boolean isOwner(Long ownerId) {
        return hasAccessToResource(ownerId);
    }

    /**
     * Checks if the current user matches the provided user ID.
     *
     * @param userId the user ID to check
     * @return true if current user matches the provided ID, false otherwise
     */
    public boolean isSameUser(Long userId) {
        return hasAccessToResource(userId);
    }

    /**
     * Example method for checking access to a store.
     * Customize this based on your business logic.
     *
     * @param storeId the store ID to check access for
     * @return true if user has access to the store, false otherwise
     */
    public boolean hasAccessToStore(Long storeId) {
        Long currentUserId = getCurrentUserId();

        if (currentUserId == null || storeId == null) {
            return false;
        }

        // TODO: Implement your store access logic here
        // This might involve checking:
        // - If user owns the store
        // - If user is an employee of the store
        // - If user has been granted specific permissions

        log.debug("Checking store access: userId={}, storeId={}", currentUserId, storeId);

        // Placeholder implementation - customize based on your needs
        return true;
    }

    /**
     * Example method for checking if user can modify a post.
     *
     * @param postOwnerId the ID of the post owner
     * @return true if user can modify the post, false otherwise
     */
    public boolean canModifyPost(Long postOwnerId) {
        return hasAccessToResource(postOwnerId);
    }

    /**
     * Example method for checking if user can delete a comment.
     *
     * @param commentOwnerId the ID of the comment owner
     * @return true if user can delete the comment, false otherwise
     */
    public boolean canDeleteComment(Long commentOwnerId) {
        return hasAccessToResource(commentOwnerId);
    }
}
