package com.github.technoir42.glide.preloader;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.flexbox.FlexboxLayoutManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultVisiblePositionFinderTest {
    private final DefaultVisiblePositionFinder visiblePositionFinder = new DefaultVisiblePositionFinder();

    @Nested
    @DisplayName("Unsupported LayoutManager")
    class UnsupportedLayoutManagerTest {
        @Test
        @DisplayName("findFirstVisibleItemPosition throws IllegalStateException")
        void findFirstVisibleItemPosition() {
            RecyclerView.LayoutManager layoutManager = new MockLayoutManager();

            Exception e = assertThrows(IllegalStateException.class, () -> visiblePositionFinder.findFirstVisibleItemPosition(layoutManager));
            assertEquals("Unsupported LayoutManager: com.github.technoir42.glide.preloader.MockLayoutManager", e.getMessage());
        }

        @Test
        @DisplayName("findLastVisibleItemPosition throws IllegalStateException")
        void findLastVisibleItemPosition() {
            RecyclerView.LayoutManager layoutManager = new MockLayoutManager();

            Exception e = assertThrows(IllegalStateException.class, () -> visiblePositionFinder.findLastVisibleItemPosition(layoutManager));
            assertEquals("Unsupported LayoutManager: com.github.technoir42.glide.preloader.MockLayoutManager", e.getMessage());
        }
    }

    @Nested
    @DisplayName("LinearLayoutManager")
    class LinearLayoutManagerTest {
        @Test
        void findFirstVisibleItemPosition() {
            LinearLayoutManager layoutManager = mock(LinearLayoutManager.class);
            when(layoutManager.findFirstVisibleItemPosition()).thenReturn(42);

            int result = visiblePositionFinder.findFirstVisibleItemPosition(layoutManager);

            assertEquals(42, result);
        }

        @Test
        void findLastVisibleItemPosition() {
            LinearLayoutManager layoutManager = mock(LinearLayoutManager.class);
            when(layoutManager.findLastVisibleItemPosition()).thenReturn(42);

            int result = visiblePositionFinder.findLastVisibleItemPosition(layoutManager);

            assertEquals(42, result);
        }
    }

    @Nested
    @DisplayName("StaggeredGridLayoutManager")
    class StaggeredGridLayoutManagerTest {
        @Test
        void findFirstVisibleItemPosition() {
            StaggeredGridLayoutManager layoutManager = mock(StaggeredGridLayoutManager.class);
            int[] positions = {-1, 0, 1};
            when(layoutManager.getSpanCount()).thenReturn(positions.length);
            when(layoutManager.findFirstVisibleItemPositions(any())).thenAnswer(copyToArgument(positions));

            int result = visiblePositionFinder.findFirstVisibleItemPosition(layoutManager);

            assertEquals(0, result);
        }

        @Test
        void findLastVisibleItemPosition() {
            StaggeredGridLayoutManager layoutManager = mock(StaggeredGridLayoutManager.class);
            int[] positions = {-1, 2, 1};
            when(layoutManager.getSpanCount()).thenReturn(positions.length);
            when(layoutManager.findLastVisibleItemPositions(any())).thenAnswer(copyToArgument(positions));

            int result = visiblePositionFinder.findLastVisibleItemPosition(layoutManager);

            assertEquals(2, result);
        }

        private Answer copyToArgument(int[] source) {
            return invocation -> {
                int[] into = invocation.getArgument(0);
                System.arraycopy(source, 0, into, 0, source.length);
                return into;
            };
        }
    }

    @Nested
    @DisplayName("FlexboxLayoutManager")
    class FlexboxLayoutManagerTest {
        @Test
        void findFirstVisibleItemPosition() {
            FlexboxLayoutManager layoutManager = mock(FlexboxLayoutManager.class);
            when(layoutManager.findFirstVisibleItemPosition()).thenReturn(42);

            int result = visiblePositionFinder.findFirstVisibleItemPosition(layoutManager);

            assertEquals(42, result);
        }

        @Test
        void findLastVisibleItemPosition() {
            FlexboxLayoutManager layoutManager = mock(FlexboxLayoutManager.class);
            when(layoutManager.findLastVisibleItemPosition()).thenReturn(42);

            int result = visiblePositionFinder.findLastVisibleItemPosition(layoutManager);

            assertEquals(42, result);
        }
    }
}

class MockLayoutManager extends RecyclerView.LayoutManager {
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return null;
    }
}
