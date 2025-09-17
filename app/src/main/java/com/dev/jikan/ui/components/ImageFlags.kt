package com.dev.jikan.ui.components

/**
 * Simple class to manage image display flags for legal constraint handling.
 * This addresses the assignment requirement to handle cases where images
 * cannot be displayed due to legal changes.
 */
object ImageFlags {

    // Global flag to hide all images
    var hideAllImages = false

    // Specific flags for different image types
    var hideAnimePosters = false
    var hideCharacterImages = false
    var hideTrailerThumbnails = false
    var hideStudioLogos = false

    /**
     * Check if anime posters should be hidden
     */
    fun shouldHideAnimePoster(): Boolean = hideAllImages || hideAnimePosters

    /**
     * Check if character images should be hidden
     */
    fun shouldHideCharacterImage(): Boolean = hideAllImages || hideCharacterImages

    /**
     * Check if trailer thumbnails should be hidden
     */
    fun shouldHideTrailerThumbnail(): Boolean = hideAllImages || hideTrailerThumbnails

    /**
     * Check if studio logos should be hidden
     */
    fun shouldHideStudioLogo(): Boolean = hideAllImages || hideStudioLogos

    /**
     * Toggle all images on/off
     */
    fun toggleAllImages() {
        hideAllImages = !hideAllImages
    }

    /**
     * Reset all flags to false (show all images)
     */
    fun showAllImages() {
        hideAllImages = false
        hideAnimePosters = false
        hideCharacterImages = false
        hideTrailerThumbnails = false
        hideStudioLogos = false
    }

    /**
     * Hide all images (simulate legal constraint)
     */
    fun hideAllImages() {
        hideAllImages = true
    }
}
