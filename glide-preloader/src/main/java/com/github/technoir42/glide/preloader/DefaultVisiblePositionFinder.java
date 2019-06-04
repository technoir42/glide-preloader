package com.github.technoir42.glide.preloader;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.flexbox.FlexboxLayoutManager;

public class DefaultVisiblePositionFinder implements VisiblePositionFinder {
    static final DefaultVisiblePositionFinder INSTANCE = new DefaultVisiblePositionFinder();
    private int[] buffer;

    @SuppressWarnings("WeakerAccess")
    protected DefaultVisiblePositionFinder() {
    }

    @Override
    public int findFirstVisibleItemPosition(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return findFirstVisibleItemPosition((StaggeredGridLayoutManager) layoutManager);
        } else {
            try {
                if (layoutManager instanceof FlexboxLayoutManager) {
                    return ((FlexboxLayoutManager) layoutManager).findFirstVisibleItemPosition();
                }
            } catch (NoClassDefFoundError ignored) {
            }
            throw new IllegalStateException("Unsupported LayoutManager: " + layoutManager.getClass().getName());
        }
    }

    @Override
    public int findLastVisibleItemPosition(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return findLastVisibleItemPosition((StaggeredGridLayoutManager) layoutManager);
        } else {
            try {
                if (layoutManager instanceof FlexboxLayoutManager) {
                    return ((FlexboxLayoutManager) layoutManager).findLastVisibleItemPosition();
                }
            } catch (NoClassDefFoundError ignored) {
            }
            throw new IllegalStateException("Unsupported LayoutManager: " + layoutManager.getClass().getName());
        }
    }

    private int findFirstVisibleItemPosition(StaggeredGridLayoutManager layoutManager) {
        int spanCount = layoutManager.getSpanCount();
        int[] positions = layoutManager.findFirstVisibleItemPositions(getBuffer(spanCount));
        return minPosition(positions, spanCount);
    }

    private int findLastVisibleItemPosition(StaggeredGridLayoutManager layoutManager) {
        int spanCount = layoutManager.getSpanCount();
        int[] positions = layoutManager.findLastVisibleItemPositions(getBuffer(spanCount));
        return maxPosition(positions, spanCount);
    }

    private int minPosition(int[] positions, int size) {
        int min = RecyclerView.NO_POSITION;
        for (int i = 0; i < size; i++) {
            int position = positions[i];
            if (position != RecyclerView.NO_POSITION && (position < min || min == RecyclerView.NO_POSITION)) {
                min = position;
            }
        }
        return min;
    }

    private int maxPosition(int[] positions, int size) {
        int max = RecyclerView.NO_POSITION;
        for (int i = 0; i < size; i++) {
            int position = positions[i];
            if (position != RecyclerView.NO_POSITION && position > max) {
                max = position;
            }
        }
        return max;
    }

    private int[] getBuffer(int size) {
        if (buffer == null || buffer.length < size) {
            buffer = new int[size];
        }
        return buffer;
    }
}
