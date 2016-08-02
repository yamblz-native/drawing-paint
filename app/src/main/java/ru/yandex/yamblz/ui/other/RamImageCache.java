package ru.yandex.yamblz.ui.other;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class RamImageCache implements ImageCache {

    private Map<String, Bitmap> mCache = new HashMap<>();

    @Override
    public void put(String tag, Bitmap image) {
        mCache.put(tag, image);
    }

    @Nullable
    @Override
    public Bitmap get(String tag) {
        return mCache.get(tag);
    }

    @Nullable
    @Override
    public Bitmap remove(String tag) {
        return mCache.remove(tag);
    }
}
