package ru.shmakova.painter.draw.filter;

import android.support.annotation.IdRes;

import rx.Observable;

public interface FilterView {
    Observable<Integer> submitClicks();

    void sendBackResult(@IdRes int filter);

    void dismissDialog();
}
