package ru.shmakova.painter.draw.brush;

import rx.Observable;

public interface BrushView {
    Observable<Integer> submitClicks();

    void sendBackResult(float strokeWidth);

    void setStrokeWidth(float savedStrokeWidth);

    void dismissDialog();
}
