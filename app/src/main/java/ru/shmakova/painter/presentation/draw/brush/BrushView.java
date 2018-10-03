package ru.shmakova.painter.presentation.draw.brush;

import android.support.annotation.NonNull;

import rx.Observable;

public interface BrushView {
    @NonNull
    Observable<Integer> submitClicks();

    void sendBackResult(float strokeWidth);

    void setStrokeWidth(float savedStrokeWidth);

    void dismissDialog();
}
