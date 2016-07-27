package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.views.ColorDialog;
import ru.yandex.yamblz.ui.views.DrawView;
import ru.yandex.yamblz.ui.views.PaintSizeDialog;

public class ContentFragment extends BaseFragment {
    private ColorDialog colorDialog;
    private PaintSizeDialog paintSizeDialog;
    @BindView(R.id.select_color) TextView selectColor;
    @BindView(R.id.select_paint_size) TextView selectPaintSize;
    @BindView(R.id.draw_view) DrawView drawView;
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(view);
        colorDialog = new ColorDialog(getContext());
        colorDialog.setColorChangedListener(color -> drawView.setColor(color));
        selectColor.setOnClickListener(v -> colorDialog.show());

        paintSizeDialog = new PaintSizeDialog(getContext());
        paintSizeDialog.setSizeChangedListener(size->drawView.setSize(size));
        selectPaintSize.setOnClickListener(v -> paintSizeDialog.show(drawView.getSize()));

    }
}
