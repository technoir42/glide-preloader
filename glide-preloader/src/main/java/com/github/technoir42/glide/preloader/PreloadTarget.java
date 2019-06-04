package com.github.technoir42.glide.preloader;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

final class PreloadTarget implements Target<Object> {
    @Nullable
    private Request request;
    @VisibleForTesting
    int width;
    @VisibleForTesting
    int height;

    void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void onLoadStarted(@Nullable Drawable placeholder) {
    }

    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
    }

    @Override
    public void onResourceReady(@NonNull Object resource, @Nullable Transition<? super Object> transition) {
    }

    @Override
    public void onLoadCleared(@Nullable Drawable placeholder) {
    }

    @Override
    public void getSize(@NonNull SizeReadyCallback cb) {
        cb.onSizeReady(width, height);
    }

    @Override
    public void removeCallback(@NonNull SizeReadyCallback cb) {
    }

    @Override
    public void setRequest(@Nullable Request request) {
        this.request = request;
    }

    @Nullable
    @Override
    public Request getRequest() {
        return request;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onDestroy() {
    }
}
