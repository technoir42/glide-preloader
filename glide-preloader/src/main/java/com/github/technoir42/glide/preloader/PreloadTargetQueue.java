package com.github.technoir42.glide.preloader;

import java.util.ArrayDeque;
import java.util.Queue;

import static com.github.technoir42.glide.preloader.Util.checkNotNull;

final class PreloadTargetQueue {
    private final Queue<PreloadTarget> queue;

    PreloadTargetQueue(int size) {
        queue = new ArrayDeque<>(size);
        for (int i = 0; i < size; i++) {
            queue.offer(new PreloadTarget());
        }
    }

    PreloadTarget next(int width, int height) {
        final PreloadTarget result = checkNotNull(queue.poll(), "Preload queue is empty");
        queue.offer(result);
        result.setSize(width, height);
        return result;
    }

    int size() {
        return queue.size();
    }
}
