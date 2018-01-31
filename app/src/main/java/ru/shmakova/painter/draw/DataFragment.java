package ru.shmakova.painter.draw;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;

import ru.shmakova.painter.screen.BaseFragment;

public class DataFragment extends BaseFragment {
    public static final String TAG = "data";

    private Bitmap data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setData(@NonNull Bitmap data) {
        this.data = data;
    }

    public Bitmap getData() {
        return data;
    }
}
