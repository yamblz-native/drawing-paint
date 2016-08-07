package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.other.PaintMode;
import ru.yandex.yamblz.ui.views.CanvasView;
import yuku.ambilwarna.AmbilWarnaDialog;

public class ContentFragment extends BaseFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        CanvasView canvasView = (CanvasView) view.findViewById(R.id.canvasView);
        ImageView imgBrush = ButterKnife.findById(view,R.id.brush);
        ImageView imgColor = ButterKnife.findById(view,R.id.color);
        ImageView imgClear = ButterKnife.findById(view,R.id.clear);

        View.OnClickListener iconsClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.brush:
                        setBrushMode(canvasView);
                        break;
                    case R.id.color:
                        chooseColor(canvasView);
                        break;
                    case R.id.clear:
                        clearCanvas(canvasView);
                        break;
                    default:
                        Log.e("DASHES", ""+v.getId());
                }

            }
        };
        imgBrush.setOnClickListener(iconsClickListener);
        imgColor.setOnClickListener(iconsClickListener);
        imgClear.setOnClickListener(iconsClickListener);
        return view;
    }

    private void setBrushMode(CanvasView view){
        view.setMode(PaintMode.BRUSH_MODE);
    }

    private void clearCanvas(CanvasView view){
        view.clear(view.getWidth(),view.getHeight());
    }

    private void chooseColor(CanvasView view){
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(getContext(), 0xff000000, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                view.setColor(color);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }
        });
        dialog.show();
    }

}
