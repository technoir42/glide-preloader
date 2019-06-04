package com.github.technoir42.glide.preloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

final class Util {
    private Util() {
    }

    @NonNull
    static <T> T checkNotNull(@Nullable T object, @NonNull String message) {
        if (object == null) {
            throw new IllegalStateException(message);
        }
        return object;
    }

    @NonNull
    static <T> T requireNonNull(@Nullable T object, @NonNull String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    static void require(boolean condition, Supplier<String> lazyMessage) {
        if (!condition) {
            throw new IllegalArgumentException(lazyMessage.get());
        }
    }

    interface Supplier<T> {
        T get();
    }
}
