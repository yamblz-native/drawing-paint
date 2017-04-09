package ru.shmakova.painter.draw;

import android.support.annotation.ColorInt;

import rx.Observable;

public interface DrawView {
    Observable<Integer> colorPicks();

    void setStrokeWidth(float savedStrokeWidth);

    void setColor(@ColorInt int color);

    void updateMenuColor(@ColorInt int color);
}
