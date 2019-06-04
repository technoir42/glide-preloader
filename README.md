Glide Preloader
===============

[ ![Download](https://api.bintray.com/packages/sch/maven/glide-preloader/images/download.svg) ](https://bintray.com/sch/maven/glide-preloader/_latestVersion)

Simple and minimalistic API for pre-loading images in scrollable lists using Glide.

## Usage

```gradle
repositories {
    maven {
        url "https://dl.bintray.com/sch/maven"
    }
}

dependencies {
    implementation "com.github.technoir42:glide-preloader:0.1"
}
```

### Kotlin

```kotlin
val callback = object : ListPreloader.Callback {
    override fun onPreload(position: Int, preloader: ListPreloader) {
        val item = adapter.getItem(position)

        // Configure pre-load request similarly to the main request.
        val preloadRequest = glide.load(item.imageUrl)
            .circleCrop()
            .priority(Priority.LOW) // Setting priority to LOW is optional but recommended.

        // Call `preload` passing the request and dimensions of the target.
        // You can call `preload` multiple times if there are multiple images per item
        // or you can skip this call entirely if there is nothing to pre-load.
        preloader.preload(preloadRequest, width, height)
    }
}

ListPreloader(glide, callback, MAX_PRELOAD).attach(recyclerView)
```

### Java

```java
ListPreloader.Callback callback = (position, preloader) -> {
    MyListItem item = adapter.getItem(position);

    // Configure pre-load request similarly to the main request.
    RequestBuilder<Drawable> preloadRequest = glide.load(item.getImageUrl())
        .circleCrop()
        .priority(Priority.LOW); // Setting priority to LOW is optional but recommended.

    // Call `preload` passing the request and dimensions of the target.
    // You can call `preload` multiple times if there are multiple images per item
    // or you can skip this call entirely if there is nothing to pre-load.
    preloader.preload(preloadRequest, width, height);
};

new ListPreloader(glide, callback, MAX_PRELOAD).attach(recyclerView);
```

## License

```
Copyright 2019 Sergey Chelombitko

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
