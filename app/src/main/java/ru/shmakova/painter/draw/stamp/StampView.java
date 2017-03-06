package ru.shmakova.painter.draw.stamp;

import rx.Observable;

public interface StampView {
    Observable<Integer> submitClicks();

    void sendBackResult(int stamp);

    void dismissDialog();
}
