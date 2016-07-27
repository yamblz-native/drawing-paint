package ru.yandex.yamblz.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.activities.MainActivity;
import ru.yandex.yamblz.ui.views.ColorDialog;
import ru.yandex.yamblz.ui.views.DrawView;
import ru.yandex.yamblz.ui.views.PaintSizeDialog;

public class ContentFragment extends BaseFragment {
    private ColorDialog colorDialog;
    private PaintSizeDialog paintSizeDialog;
    @BindView(R.id.select_color) TextView selectColor;
    @BindView(R.id.select_paint_size) TextView selectPaintSize;
    @BindView(R.id.draw_view) DrawView drawView;
    @BindView(R.id.save) TextView saveView;
    @BindView(R.id.load) TextView loadView;
    @BindView(R.id.rainbow_checkbox) CheckBox checkBox;
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

        saveView.setOnClickListener(v -> {
            MediaStore.Images.Media.insertImage(getContext().getContentResolver(), drawView.getBitmap(), "MyDraw"+new Date().toString(), "awesome img");
        });
        loadView.setOnClickListener(v->{
            ((MainActivity)getActivity()).loadImage();
        });
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            drawView.setEnableRainbow(isChecked);
        });
    }

    public void setBitmap(Bitmap bitmap) {
        drawView.setBitmap(bitmap);
    }
}
