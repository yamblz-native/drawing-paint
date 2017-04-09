package ru.shmakova.painter.draw.stamp;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import ru.shmakova.painter.screen.BasePresenter;

public class StampPresenter extends BasePresenter<StampView> {

    @Inject
    StampPresenter() {
        // No op
    }

    @Override
    public void bindView(@NonNull StampView view) {
        super.bindView(view);

        unsubscribeOnUnbindView(
                view().submitClicks()
                        .subscribe(stamp -> {
                            view().sendBackResult(stamp);
                            view().dismissDialog();
                        })
        );
    }
}
