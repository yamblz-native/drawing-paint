package ru.yandex.yamblz.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.views.CanvasView;

public class ContentFragment extends BaseFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        ImageView imgColor = (ImageView) view.findViewById(R.id.color);
        CanvasView canvasView = (CanvasView) view.findViewById(R.id.canvasView);
        imgColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.setColor(Color.GREEN);
            }
        });
        return view;
    }
}
