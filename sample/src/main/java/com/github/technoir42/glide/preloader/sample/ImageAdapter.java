package com.github.technoir42.glide.preloader.sample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.github.technoir42.glide.debug.indicator.DebugIndicatorTransitionFactory;

import java.util.Locale;

class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private final RequestManager requestManager;

    ImageAdapter(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = getImageUrl(position);

        requestManager.load(imageUrl)
                .transition(DrawableTransitionOptions.with(DebugIndicatorTransitionFactory.DEFAULT))
                .into(holder.imageView);
    }

    String getImageUrl(int position) {
        return String.format(Locale.ROOT, "https://placeimg.com/320/160/any?position=%d", position);
    }

    @Override
    public int getItemCount() {
        return 100;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}
