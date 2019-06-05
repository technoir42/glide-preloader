package com.github.technoir42.glide.preloader;

import androidx.recyclerview.widget.RecyclerView;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecyclerViewAttacherTest {
    private final ListPreloader preloader = mock(ListPreloader.class);
    private final RecyclerView recyclerView = mock(RecyclerView.class, RETURNS_DEEP_STUBS);
    private final VisiblePositionFinder visiblePositionFinder = mock(VisiblePositionFinder.class);
    private final RecyclerViewAttacher attacher = new RecyclerViewAttacher(recyclerView, visiblePositionFinder);

    @Test
    void attach() {
        attacher.attach(preloader);

        verify(recyclerView).addOnScrollListener(attacher);
    }

    @Test
    void detach() {
        attacher.attach(preloader);

        attacher.detach();

        verify(recyclerView).removeOnScrollListener(attacher);
    }

    @Test
    @DisplayName("onScrolled calls onScrolled on ListPreloader")
    @SuppressWarnings("ConstantConditions")
    void onScrolled() {
        attacher.attach(preloader);
        when(visiblePositionFinder.findFirstVisibleItemPosition(any())).thenReturn(1);
        when(visiblePositionFinder.findLastVisibleItemPosition(any())).thenReturn(2);
        when(recyclerView.getAdapter().getItemCount()).thenReturn(10);

        attacher.onScrolled(recyclerView, 0, 100);

        verify(preloader).onScrolled(1, 2, 10);
    }

    @Test
    @DisplayName("onScrolled throws IllegalStateException it not attached")
    void onScrolled_notAttached() {
        assertThrows(IllegalStateException.class, () -> attacher.onScrolled(recyclerView, 0, 100), "Not attached");
    }
}
