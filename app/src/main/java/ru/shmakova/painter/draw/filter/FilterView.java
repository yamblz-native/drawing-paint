package ru.shmakova.painter.draw.filter;

import rx.Observable;

public interface FilterView {
    Observable<Integer> submitClicks();

    void sendBackResult(int filter);

    void dismissDialog();
}
