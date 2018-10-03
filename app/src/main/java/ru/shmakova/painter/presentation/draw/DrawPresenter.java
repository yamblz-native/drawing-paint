package ru.shmakova.painter.presentation.draw;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import javax.inject.Inject;

import ru.shmakova.painter.R;
import ru.shmakova.painter.presentation.base.BasePresenter;

import static ru.shmakova.painter.presentation.draw.brush.BrushPresenter.STROKE_WIDTH;

public class DrawPresenter extends BasePresenter<DrawView> {
    private static final String COLOR_KEY = "COLOR_KEY";

    @NonNull
    private final SharedPreferences sharedPreferences;
    @NonNull
    private final Context context;

    @Inject
    DrawPresenter(@NonNull SharedPreferences sharedPreferences, @NonNull Context context) {
        this.sharedPreferences = sharedPreferences;
        this.context = context;
    }

    @Override
    public void bindView(@NonNull DrawView view) {
        super.bindView(view);
        view().setStrokeWidth(sharedPreferences.getFloat(STROKE_WIDTH, 5f));
        int savedColor = sharedPreferences.getInt(COLOR_KEY, ContextCompat.getColor(context, R.color.black));
        view().updateMenuColor(savedColor);
        view().setColor(savedColor);

        unsubscribeOnUnbindView(
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
