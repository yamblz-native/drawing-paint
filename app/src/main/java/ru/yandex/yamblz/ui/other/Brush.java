package ru.yandex.yamblz.ui.other;

import android.graphics.Path;

/**
 * Created by dsukmanova on 07.08.16.
 */

public class Brush {
    public void actionDown(Path path, float x, float y) {
        path.moveTo(x,y);
    }

    public void actionMove(Path path, float x, float y) {
        path.lineTo(x,y);
    }

    public void actionUp(Path path, float x, float y) {
        path.lineTo(x,y);
    }
}
