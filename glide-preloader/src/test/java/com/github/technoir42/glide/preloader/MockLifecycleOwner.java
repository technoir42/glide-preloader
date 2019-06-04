package com.github.technoir42.glide.preloader;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

final class MockLifecycleOwner implements LifecycleOwner {
    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    MockLifecycleOwner(Lifecycle.State initialState) {
        moveToState(initialState);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    void moveToState(Lifecycle.State state) {
        lifecycleRegistry.markState(state);
    }

    boolean hasObservers() {
        return lifecycleRegistry.getObserverCount() > 0;
    }
}
