package ru.shmakova.painter.draw;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import javax.inject.Inject;

import ru.shmakova.painter.R;
import ru.shmakova.painter.screen.BasePresenter;
import rx.Observable;

import static ru.shmakova.painter.draw.brush.BrushPresenter.STROKE_WIDTH;

public class DrawPresenter extends BasePresenter<DrawView> {
    public final static String COLOR_KEY = "COLOR_KEY";

    @NonNull
    private final SharedPreferences sharedPreferences;
    @NonNull
    private final Application application;

    @Inject
    DrawPresenter(@NonNull SharedPreferences sharedPreferences, @NonNull Application application) {
        this.sharedPreferences = sharedPreferences;
        this.application = application;
    }

    @Override
    public void bindView(@NonNull DrawView view) {
        super.bindView(view);

        unsubscribeOnUnbindView(
                Observable.just(1)
                        .map(o -> sharedPreferences.getFloat(STROKE_WIDTH, 5f))
                        .subscribe(savedStrokeWidth -> view().setStrokeWidth(savedStrokeWidth)),
                Observable.just(1)
                        .map(o -> sharedPreferences.getInt(COLOR_KEY, ContextCompat.getColor(application, R.color.md_black)))
                        .subscribe(color -> {
                            view().updateMenuColor(color);
                            view().setColor(color);
                        }),
                view().colorPicks()
                        .doOnNext(color -> sharedPreferences
                                .edit()
                                .putInt(COLOR_KEY, color)
                                .apply())
                        .subscribe(color -> {
                            view().updateMenuColor(color);
                            view().setColor(color);
                        })
        );
    }
}
