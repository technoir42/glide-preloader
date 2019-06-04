package com.github.technoir42.glide.preloader.sample;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.github.technoir42.glide.preloader.ListPreloader;
import com.jakewharton.processphoenix.ProcessPhoenix;

public class MainActivity extends AppCompatActivity {
    private static final int MAX_PRELOAD = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RequestManager glide = Glide.with(this);
        ImageAdapter adapter = new ImageAdapter(glide);

        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ListPreloader.Callback callback = (position, preloader) -> {
            String imageUrl = adapter.getImageUrl(position);
            int itemWidth = recyclerView.getWidth();
            int itemHeight = itemWidth / 2;

            RequestBuilder<Drawable> preloadRequest = glide.load(imageUrl)
                    .priority(Priority.LOW)
                    .skipMemoryCache(true);

            preloader.preload(preloadRequest, itemWidth, itemHeight);
        };

        new ListPreloader(glide, callback, MAX_PRELOAD).attach(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_clear_cache) {
            clearCache();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearCache() {
        new Thread(() -> {
            Glide.get(this).clearDiskCache();
            runOnUiThread(() -> ProcessPhoenix.triggerRebirth(this));
        }).start();
    }
}
