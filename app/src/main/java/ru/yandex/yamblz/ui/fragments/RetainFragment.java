package ru.yandex.yamblz.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import ru.yandex.yamblz.ui.other.ImageInterface;

/**
 * Created by Volha on 07.08.2016.
 */

public class RetainFragment extends Fragment implements ImageInterface {

    public static final String TAG = "retain_fragment_tag";
    private Bitmap savedImage;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public Bitmap getImage() {
        return savedImage;
    }

    @Override
    public void setImage(Bitmap bitmap) {
        this.savedImage = bitmap;
    }



}
