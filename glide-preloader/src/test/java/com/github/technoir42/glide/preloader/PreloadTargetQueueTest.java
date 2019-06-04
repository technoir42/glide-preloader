package com.github.technoir42.glide.preloader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PreloadTargetQueueTest {
    @Test
    void size() {
        PreloadTargetQueue queue = new PreloadTargetQueue(5);

        assertEquals(5, queue.size());
    }

    @Test
    @DisplayName("next sets target size")
    void next() {
        PreloadTargetQueue queue = new PreloadTargetQueue(5);

        PreloadTarget target = queue.next(200, 100);

        assertEquals(200, target.width);
        assertEquals(100, target.height);
    }
}
