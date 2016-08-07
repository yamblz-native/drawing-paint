package ru.yandex.yamblz.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.other.ImageExtractor;
import ru.yandex.yamblz.ui.other.PaintMode;
import ru.yandex.yamblz.ui.views.CanvasView;
import yuku.ambilwarna.AmbilWarnaDialog;

public class ContentFragment extends BaseFragment {
    ImageExtractor imageExtractor;
    View lastSelected = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageExtractor = new ImageExtractor();
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        CanvasView canvasView = (CanvasView) view.findViewById(R.id.canvasView);
        ImageView imgBrush = ButterKnife.findById(view,R.id.brush);
        ImageView imgColor = ButterKnife.findById(view,R.id.color);
        ImageView imgClear = ButterKnife.findById(view,R.id.clear);
        ImageView saveImg = ButterKnife.findById(view,R.id.saveimg);
        ImageView loadImg = ButterKnife.findById(view,R.id.loadimg);

        View.OnClickListener iconsClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastSelected != null){
                    lastSelected.setBackgroundColor(Color.WHITE);
                }
                v.setBackgroundColor(Color.GRAY);
                lastSelected = v;
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
                    case R.id.saveimg:
                        saveImage(canvasView);
                        showMessage(imageExtractor.getImageSaved());
                        break;
                    case R.id.loadimg:
                        if (loadImage() != null){
                            canvasView.setCurrentBitmap(loadImage());
                            showMessage(imageExtractor.getImageLoaded());
                        } else {
                            showMessage(imageExtractor.getErrorMessage());
                        }
                        break;
                    default:
                        break;
                }

            }
        };
        imgBrush.setOnClickListener(iconsClickListener);
        imgColor.setOnClickListener(iconsClickListener);
        imgClear.setOnClickListener(iconsClickListener);
        saveImg.setOnClickListener(iconsClickListener);
        loadImg.setOnClickListener(iconsClickListener);
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

    private void saveImage(CanvasView view){
        imageExtractor.saveBitmap(view.getCurrentBitmap());
    }

    private Bitmap loadImage(){
        return imageExtractor.loadBitmap();
    }

    private void showMessage(String text){
        Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
    }


}
