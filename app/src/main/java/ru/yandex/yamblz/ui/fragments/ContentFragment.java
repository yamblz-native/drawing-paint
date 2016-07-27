package ru.yandex.yamblz.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private List<String> filters;
    private Map<String, ColorFilter> colorFilterMap;
    @BindView(R.id.select_color) TextView selectColor;
    @BindView(R.id.select_paint_size) TextView selectPaintSize;
    @BindView(R.id.draw_view) DrawView drawView;
    @BindView(R.id.save) TextView saveView;
    @BindView(R.id.load) TextView loadView;
    @BindView(R.id.rainbow_checkbox) CheckBox checkBox;
    @BindView(R.id.spinner) Spinner spinner;

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
        paintSizeDialog.setSizeChangedListener(size -> drawView.setSize(size));
        selectPaintSize.setOnClickListener(v -> paintSizeDialog.show(drawView.getSize()));

        saveView.setOnClickListener(v -> {
            MediaStore.Images.Media.insertImage(getContext().getContentResolver(), drawView.getBitmap(), "MyDraw" + new Date().toString(), "awesome img");
        });
        loadView.setOnClickListener(v -> {
            ((MainActivity) getActivity()).loadImage();
        });
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            drawView.setEnableRainbow(isChecked);
        });
        initColorFiltersMap();
        filters = new ArrayList<>(colorFilterMap.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, filters);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(filters.indexOf("No filter"));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                drawView.setColorFilter(colorFilterMap.get(filters.get(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }

    private void initColorFiltersMap() {
        colorFilterMap = new HashMap<>();
        colorFilterMap.put("No filter", null);
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        colorFilterMap.put("GrayScale", new ColorMatrixColorFilter(colorMatrix));
        colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrix colorScale = new ColorMatrix();
        colorScale.setScale(1, 1, 0.8f, 1);
        // Convert to grayscale, then apply brown color
        colorMatrix.postConcat(colorScale);
        colorFilterMap.put("Sepia", new ColorMatrixColorFilter(colorMatrix));
        colorFilterMap.put("Invert", new ColorMatrixColorFilter(new ColorMatrix(new float[]{
                -1, 0, 0, 0, 255,
                0, -1, 0, 0, 255,
                0, 0, -1, 0, 255,
                0, 0, 0, 1, 0,
        })));
    }

    public void setBitmap(Bitmap bitmap) {
        drawView.setBitmap(bitmap);
    }
}
