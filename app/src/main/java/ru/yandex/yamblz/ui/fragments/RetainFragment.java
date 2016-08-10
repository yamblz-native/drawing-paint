package ru.yandex.yamblz.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;

import java.util.List;

public class RetainFragment extends Fragment {
    private List<Pair<Path, Paint>> mPathList = null;
    private Bitmap mBitmap = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setPaths(List<Pair<Path, Paint>> pathList) {
        mPathList = pathList;
    }

    public List<Pair<Path, Paint>> getPaths() {
        return mPathList;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}
