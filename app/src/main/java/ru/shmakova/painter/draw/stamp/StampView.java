package ru.shmakova.painter.draw.stamp;

import android.support.annotation.IdRes;

import rx.Observable;

public interface StampView {
    Observable<Integer> submitClicks();

    void sendBackResult(@IdRes int stamp);

    void dismissDialog();
}
