package com.github.technoir42.glide.preloader;

import androidx.lifecycle.Lifecycle;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.Target;
import com.github.technoir42.glide.preloader.ListPreloader.Attacher;
import com.github.technoir42.glide.preloader.ListPreloader.Callback;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

class ListPreloaderTest {
    private final RequestManager requestManager = mock(RequestManager.class);
    private final Callback callback = mock(Callback.class);
    private final ListPreloader preloader = new ListPreloader(requestManager, callback, 3);

    @Test
    @DisplayName("attach")
    void attach() {
        Attacher attacher = mock(Attacher.class);
        preloader.attach(attacher);

        verify(attacher, only()).attach(preloader);
    }

    @Test
    @DisplayName("attach detaches previous Attacher")
    void attach_detachesPrevious() {
        Attacher attacher = mock(Attacher.class);
        preloader.attach(attacher);

        preloader.attach(mock(Attacher.class));

        verify(attacher).detach();
    }

    @Test
    @DisplayName("detach")
    void detach() {
        Attacher attacher = mock(Attacher.class);
        preloader.attach(attacher);

        preloader.detach();

        verify(attacher).detach();
    }

    @Test
    @DisplayName("detach does nothing if not attached")
    void detach_notAttached() {
        preloader.detach();
    }

    @Test
    @DisplayName("subscribeToLifecycle enables preloading if resumed")
    void subscribeToLifecycle_1() {
        MockLifecycleOwner lifecycleOwner = new MockLifecycleOwner(Lifecycle.State.RESUMED);

        preloader.subscribeToLifecycle(lifecycleOwner.getLifecycle());

        assertTrue(preloader.isEnabled);
        assertTrue(lifecycleOwner.hasObservers());
    }

    @Test
    @DisplayName("subscribeToLifecycle disables preloading if not resumed")
    void subscribeToLifecycle_2() {
        MockLifecycleOwner lifecycleOwner = new MockLifecycleOwner(Lifecycle.State.STARTED);

        preloader.subscribeToLifecycle(lifecycleOwner.getLifecycle());

        assertFalse(preloader.isEnabled);
        assertTrue(lifecycleOwner.hasObservers());
    }

    @Test
    @DisplayName("subscribeToLifecycle removes lifecycle observer on destroy")
    void subscribeToLifecycle_3() {
        MockLifecycleOwner lifecycleOwner = new MockLifecycleOwner(Lifecycle.State.RESUMED);

        preloader.subscribeToLifecycle(lifecycleOwner.getLifecycle());
        lifecycleOwner.moveToState(Lifecycle.State.DESTROYED);

        assertFalse(lifecycleOwner.hasObservers());
    }

    @Test
    @DisplayName("onScrolled validates parameters")
    void onScrolled_validation() {
        assertThrows(IllegalArgumentException.class, () -> preloader.onScrolled(-1, 1, 10),
                "firstVisibleItem must be in range [0..10), but was -1");

        assertThrows(IllegalArgumentException.class, () -> preloader.onScrolled(10, 1, 10),
                "firstVisibleItem must be in range [0..10), but was 10");

        assertThrows(IllegalArgumentException.class, () -> preloader.onScrolled(0, -1, 10),
                "lastVisibleItem must be in range [0..10), but was -1");

        assertThrows(IllegalArgumentException.class, () -> preloader.onScrolled(0, 10, 10),
                "lastVisibleItem must be in range [0..10), but was -1");

        assertThrows(IllegalArgumentException.class, () -> preloader.onScrolled(1, 0, 10),
                "firstVisibleItem (1) must be less or equal to lastVisibleItem (0)");
    }

    @Test
    @DisplayName("onScrolled does nothing if preloading is disabled")
    void onScrolled_disabled() {
        preloader.isEnabled = false;

        preloader.onScrolled(0, 2, 5);

        verifyZeroInteractions(callback);
    }

    @Test
    @DisplayName("onScrolled down when fewer items available than maxPreload")
    void onScrolled_down_1() {
        preloader.onScrolled(0, 2, 5);

        InOrder inOrder = inOrder(callback);
        inOrder.verify(callback).onPreload(3, preloader);
        inOrder.verify(callback).onPreload(4, preloader);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("onScrolled down when more items available than maxPreload")
    void onScrolled_down_2() {
        preloader.onScrolled(0, 2, 10);

        InOrder inOrder = inOrder(callback);
        inOrder.verify(callback).onPreload(3, preloader);
        inOrder.verify(callback).onPreload(4, preloader);
        inOrder.verify(callback).onPreload(5, preloader);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("onScrolled down preloads more after scrolling again in the same direction")
    void onScrolled_down_3() {
        preloader.onScrolled(0, 1, 10);
        clearInvocations(callback);

        preloader.onScrolled(2, 3, 10);

        InOrder inOrder = inOrder(callback);
        inOrder.verify(callback).onPreload(5, preloader);
        inOrder.verify(callback).onPreload(6, preloader);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("onScrolled does nothing if visible items didn't change")
    void onScrolled_noChange() {
        preloader.onScrolled(0, 2, 5);
        clearInvocations(callback);

        preloader.onScrolled(0, 2, 5);

        verifyZeroInteractions(callback);
    }

    @Test
    @DisplayName("onScrolled up when fewer items are available than maxPreload")
    void onScrolled_up_1() {
        preloader.onScrolled(3, 4, 5);
        clearInvocations(callback);

        preloader.onScrolled(2, 3, 5);

        InOrder inOrder = inOrder(callback);
        inOrder.verify(callback).onPreload(1, preloader);
        inOrder.verify(callback).onPreload(0, preloader);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("onScrolled up when more items are available than maxPreload")
    void onScrolled_up_2() {
        preloader.onScrolled(6, 9, 10);
        clearInvocations(callback);

        preloader.onScrolled(5, 9, 10);

        InOrder inOrder = inOrder(callback);
        inOrder.verify(callback).onPreload(4, preloader);
        inOrder.verify(callback).onPreload(3, preloader);
        inOrder.verify(callback).onPreload(2, preloader);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("onScrolled up preloads more after scrolling again in the same direction")
    void onScrolled_up_3() {
        preloader.onScrolled(8, 9, 10);
        clearInvocations(callback);

        preloader.onScrolled(7, 8, 10);

        InOrder inOrder = inOrder(callback);
        inOrder.verify(callback).onPreload(5, preloader);
        inOrder.verify(callback).onPreload(4, preloader);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("onScrolled cancels previous requests if scroll direction has changed")
    void onScrolled_changeDirection() {
        doAnswer(invocation -> {
            preloader.preload(mock(RequestBuilder.class), 200, 100);
            return null;
        }).when(callback).onPreload(anyInt(), any());

        preloader.onScrolled(1, 1, 10);
        preloader.onScrolled(0, 0, 10);

        verify(requestManager, times(4)).clear(any(Target.class));
        verifyNoMoreInteractions(requestManager);
    }

    @Test
    @DisplayName("preload validates parameters")
    void preload_validation() {
        RequestBuilder<?> requestBuilder = mock(RequestBuilder.class);

        assertThrows(IllegalArgumentException.class, () -> preloader.preload(requestBuilder, 0, 100),
                "width must be greater than 0, but was 0");

        assertThrows(IllegalArgumentException.class, () -> preloader.preload(requestBuilder, 100, 0),
                "height must be greater than 0, but was 0");
    }

    @Test
    @DisplayName("preload starts request")
    void preload() {
        @SuppressWarnings("unchecked")
        RequestBuilder<Object> requestBuilder = mock(RequestBuilder.class);
        preloader.preload(requestBuilder, 200, 100);

        ArgumentCaptor<PreloadTarget> target = ArgumentCaptor.forClass(PreloadTarget.class);
        verify(requestBuilder, only()).into(target.capture());

        assertEquals(200, target.getValue().width);
        assertEquals(100, target.getValue().height);
    }
}
