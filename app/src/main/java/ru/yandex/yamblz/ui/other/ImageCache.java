package ru.yandex.yamblz.ui.other;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

public interface ImageCache {

    void put(String tag, Bitmap image);

    @Nullable Bitmap get(String tag);

    @Nullable Bitmap remove(String tag);
}
