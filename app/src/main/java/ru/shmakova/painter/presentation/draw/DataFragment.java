package ru.shmakova.painter.presentation.draw;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

public class DataFragment extends Fragment {
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
