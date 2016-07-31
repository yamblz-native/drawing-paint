package ru.yandex.yamblz.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.other.ImageLoader;
import ru.yandex.yamblz.ui.views.PaintView;

public class ContentFragment extends BaseFragment {

    @BindView(R.id.paint_view)
    PaintView paintView;

    @OnClick(R.id.color1) void changeColorTo1() { paintView.setColor(Color.parseColor("#d50000")); }
    @OnClick(R.id.color2) void changeColorTo2() { paintView.setColor(Color.parseColor("#ffa000")); }
    @OnClick(R.id.color3) void changeColorTo3() { paintView.setColor(Color.parseColor("#ffea00")); }
    @OnClick(R.id.color4) void changeColorTo4() { paintView.setColor(Color.parseColor("#64dd17")); }
    @OnClick(R.id.color5) void changeColorTo5() { paintView.setColor(Color.parseColor("#6200ea")); }
    @OnClick(R.id.color6) void changeColorTo6() { paintView.setColor(Color.parseColor("#d500f9")); }
    @OnClick(R.id.color7) void changeColorTo7() { paintView.setColor(Color.parseColor("#18ffff")); }
    @OnClick(R.id.color8) void changeColorTo8() { paintView.setColor(Color.parseColor("#f500b8")); }

    @OnClick(R.id.save)
    void saveToFile() {
        new ImageLoader(getActivity().getApplicationContext())
                .setDirectoryName("images")
                .setFileName("image.png")
                .save(paintView.getBitmap());
        Toast.makeText(getActivity().getApplication(), "image save to disk", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.load)
    void loadFile() {
        Bitmap bitmap = new ImageLoader(getActivity().getApplicationContext())
                .setDirectoryName("images")
                .setFileName("image.png")
                .load();
        if (bitmap != null) {
            Toast.makeText(getActivity().getApplication(), "image load from disk", Toast.LENGTH_SHORT).show();
            paintView.setBitmap(bitmap);
        } else {
            Toast.makeText(getActivity().getApplication(), "error load image", Toast.LENGTH_SHORT).show();
        }

    }


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }


}
