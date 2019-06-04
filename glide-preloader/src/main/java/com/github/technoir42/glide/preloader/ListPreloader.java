package com.github.technoir42.glide.preloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;

import static com.github.technoir42.glide.preloader.Util.require;
import static com.github.technoir42.glide.preloader.Util.requireNonNull;

/**
 * Pre-loads resources in the direction of scrolling.
 */
public final class ListPreloader {
    private final RequestManager requestManager;
    private final Callback callback;
    private final int maxPreload;
    private final PreloadTargetQueue preloadTargetQueue;

    private int lastEnd = 0;
    private int lastStart = 0;
    private int lastFirstVisible = -1;
    @VisibleForTesting
    boolean isEnabled = true;
    private boolean isIncreasing = true;

    @Nullable
    private Attacher attacher;

    /**
     * Constructs a new instance.
     *
     * @param requestManager {@link RequestManager} that will be used to create requests.
     * @param callback       callback that will handle creating pre-load requests.
     * @param maxPreload     maximum number of items to pre-load. Must be greater than 0.
     */
    public ListPreloader(@NonNull RequestManager requestManager, @NonNull Callback callback, int maxPreload) {
        require(maxPreload > 0, () -> "maxPreload must be greater than 0, but was " + maxPreload);

        this.requestManager = requireNonNull(requestManager, "requestManager is null");
        this.callback = requireNonNull(callback, "callback is null");
        this.maxPreload = maxPreload;
        preloadTargetQueue = new PreloadTargetQueue(maxPreload + 1);
    }

    /**
     * Attaches to the provided {@link RecyclerView}.
     */
    public void attach(@NonNull RecyclerView recyclerView) {
        attach(new RecyclerViewAttacher(recyclerView));
    }

    public void attach(@NonNull Attacher attacher) {
        Attacher previousAttacher = this.attacher;
        this.attacher = requireNonNull(attacher, "attacher is null");

        if (previousAttacher != null) {
            previousAttacher.detach();
        }
        attacher.attach(this);
    }

    public void detach() {
        Attacher attacher = this.attacher;
        if (attacher != null) {
            attacher.detach();
        }
    }

    /**
     * Subscribes to the provided lifecycle, automatically enabling pre-loading when resumed and disabling otherwise.
     *
     * @param lifecycle lifecycle to subscribe to
     */
    public ListPreloader subscribeToLifecycle(@NonNull Lifecycle lifecycle) {
        isEnabled = lifecycle.getCurrentState().isAtLeast(Lifecycle.State.RESUMED);
        lifecycle.addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onResume(@NonNull LifecycleOwner owner) {
                isEnabled = true;
            }

            @Override
            public void onPause(@NonNull LifecycleOwner owner) {
                isEnabled = false;
            }

            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                lifecycle.removeObserver(this);
            }
        });
        return this;
    }

    /**
     * Notifies that the list was scrolled and starts pre-loading if necessary.
     *
     * @param firstVisibleItem index of the first visible item.
     * @param lastVisibleItem  index of the last visible item.
     * @param totalItemCount   total number of items in adapter.
     */
    public void onScrolled(int firstVisibleItem, int lastVisibleItem, int totalItemCount) {
        require(firstVisibleItem >= 0 && firstVisibleItem < totalItemCount, () ->
                "firstVisibleItem must be in range [0.." + totalItemCount + "), but was " + firstVisibleItem);
        require(lastVisibleItem >= 0 && lastVisibleItem < totalItemCount, () ->
                "lastVisibleItem must be in range [0.." + totalItemCount + "), but was " + lastVisibleItem);
        require(firstVisibleItem <= lastVisibleItem, () ->
                "firstVisibleItem (" + firstVisibleItem + ") must be less or equal to lastVisibleItem (" + lastVisibleItem + ")");

        if (!isEnabled) return;

        if (firstVisibleItem > lastFirstVisible) {
            preload(lastVisibleItem + 1, totalItemCount, true);
        } else if (firstVisibleItem < lastFirstVisible) {
            preload(firstVisibleItem, totalItemCount, false);
        }

        lastFirstVisible = firstVisibleItem;
    }

    /**
     * Enqueues the provided pre-load request.
     *
     * @param requestBuilder pre-load request.
     * @param width          width of the target. Must be greater than 0.
     * @param height         height of the target. Must be greater than 0.
     */
    @SuppressWarnings("unchecked")
    public void preload(@NonNull RequestBuilder<?> requestBuilder, int width, int height) {
        require(width > 0, () -> "width must be greater than 0, but was " + width);
        require(height > 0, () -> "height must be greater than 0, but was " + height);

        PreloadTarget target = preloadTargetQueue.next(width, height);
        ((RequestBuilder<Object>) requestBuilder).into(target);
    }

    private void preload(int start, int totalItemCount, boolean increasing) {
        if (isIncreasing != increasing) {
            isIncreasing = increasing;
            cancelAll();
        }
        preload(start, start + (increasing ? maxPreload : -maxPreload), totalItemCount);
    }

    private void preload(int from, int to, int totalItemCount) {
        int start;
        int end;
        if (from < to) {
            start = Math.max(lastEnd, from);
            end = to;
        } else {
            start = to;
            end = Math.min(lastStart, from);
        }
        end = Math.min(totalItemCount, end);
        start = Math.min(totalItemCount, Math.max(0, start));

        if (from < to) {
            for (int i = start; i < end; i++) {
                callback.onPreload(i, this);
            }
        } else {
            for (int i = end - 1; i >= start; i--) {
                callback.onPreload(i, this);
            }
        }

        lastStart = start;
        lastEnd = end;
    }

    private void cancelAll() {
        for (int i = 0; i < preloadTargetQueue.size(); i++) {
            PreloadTarget target = preloadTargetQueue.next(0, 0);
            requestManager.clear(target);
        }
    }

    public interface Attacher {
        void attach(@NonNull ListPreloader preloader);

        void detach();
    }

    public interface Callback {
        void onPreload(int position, @NonNull ListPreloader preloader);
    }
}
