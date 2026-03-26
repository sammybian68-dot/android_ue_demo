package com.example.btphone.model;

import androidx.annotation.NonNull;

/**
 * Data model representing a phone brand entry in the sync-operation dialog.
 * Each brand has a display name, detail instructions for enabling contact sync,
 * and an expanded/collapsed state.
 */
public class SyncBrand {

    @NonNull
    private final String mBrandName;

    @NonNull
    private final String mDetailText;

    private boolean mExpanded;

    /**
     * Creates a new SyncBrand instance.
     *
     * @param brandName  the display name of the phone brand
     * @param detailText the detailed sync instructions for this brand
     */
    public SyncBrand(@NonNull String brandName, @NonNull String detailText) {
        mBrandName = brandName;
        mDetailText = detailText;
        mExpanded = false;
    }

    /**
     * Returns the display name of this phone brand.
     *
     * @return brand name
     */
    @NonNull
    public String getBrandName() {
        return mBrandName;
    }

    /**
     * Returns the detailed instructions for enabling contact sync on this brand.
     *
     * @return detail text
     */
    @NonNull
    public String getDetailText() {
        return mDetailText;
    }

    /**
     * Returns whether this brand item is currently expanded.
     *
     * @return true if expanded, false if collapsed
     */
    public boolean isExpanded() {
        return mExpanded;
    }

    /**
     * Sets the expanded state of this brand item.
     *
     * @param expanded true to expand, false to collapse
     */
    public void setExpanded(boolean expanded) {
        mExpanded = expanded;
    }

    /**
     * Toggles the expanded/collapsed state.
     */
    public void toggleExpanded() {
        mExpanded = !mExpanded;
    }
}
