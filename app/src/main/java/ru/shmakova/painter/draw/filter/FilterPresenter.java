package ru.shmakova.painter.draw.filter;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import ru.shmakova.painter.screen.BasePresenter;

public class FilterPresenter extends BasePresenter<FilterView> {

    @Inject
    FilterPresenter() {
        // No op
    }

    @Override
    public void bindView(@NonNull FilterView view) {
        super.bindView(view);

        unsubscribeOnUnbindView(
                view().submitClicks()
                        .subscribe(filter -> {
                            view().sendBackResult(filter);
                            view().dismissDialog();
                        })
        );
    }
}
