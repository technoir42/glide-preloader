package com.github.technoir42.glide.preloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import static com.github.technoir42.glide.preloader.Util.checkNotNull;
import static com.github.technoir42.glide.preloader.Util.requireNonNull;

public final class RecyclerViewAttacher extends RecyclerView.OnScrollListener implements ListPreloader.Attacher {
    private final RecyclerView recyclerView;
    private final VisiblePositionFinder visiblePositionFinder;
    @Nullable
    private ListPreloader preloader;

    public RecyclerViewAttacher(@NonNull RecyclerView recyclerView) {
        this(recyclerView, DefaultVisiblePositionFinder.INSTANCE);
    }

    public RecyclerViewAttacher(@NonNull RecyclerView recyclerView, @NonNull VisiblePositionFinder visiblePositionFinder) {
        this.recyclerView = requireNonNull(recyclerView, "recyclerView is null");
        this.visiblePositionFinder = requireNonNull(visiblePositionFinder, "visiblePositionFinder is null");
    }

    @Override
    public void attach(@NonNull ListPreloader preloader) {
        this.preloader = requireNonNull(preloader, "preloader is null");
        recyclerView.addOnScrollListener(this);
    }

    @Override
    public void detach() {
        preloader = null;
        recyclerView.removeOnScrollListener(this);
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        RecyclerView.Adapter<?> adapter = checkNotNull(recyclerView.getAdapter(), "RecyclerView has no Adapter");
        RecyclerView.LayoutManager layoutManager = checkNotNull(recyclerView.getLayoutManager(), "RecyclerView has no LayoutManager");

        int totalItemCount = adapter.getItemCount();
        int firstVisible = visiblePositionFinder.findFirstVisibleItemPosition(layoutManager);
        int lastVisible = visiblePositionFinder.findLastVisibleItemPosition(layoutManager);

        ListPreloader preloader = checkNotNull(this.preloader, "Not attached");
        preloader.onScrolled(firstVisible, lastVisible, totalItemCount);
    }
}
