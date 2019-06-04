package com.github.technoir42.glide.preloader;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public interface VisiblePositionFinder {
    /**
     * Returns the adapter position of the first visible item or {@link RecyclerView#NO_POSITION} if no items are visible.
     */
    int findFirstVisibleItemPosition(@NonNull RecyclerView.LayoutManager layoutManager);

    /**
     * Returns the adapter position of the first visible item or {@link RecyclerView#NO_POSITION} if no items are visible.
     */
    int findLastVisibleItemPosition(@NonNull RecyclerView.LayoutManager layoutManager);
}
