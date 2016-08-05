package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.views.CanvasView;

public class ContentFragment extends BaseFragment implements MenuFragment.CanvasFragment {

    @BindView(R.id.canvas_view) CanvasView canvasView;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void setColor(int color) {
        canvasView.setColor(color);
    }

    @Override
    public void setTool(CanvasView.Tool tool) {
        canvasView.setTool(tool);
    }

    @Override
    public void setPrint(int drawableId) {
        canvasView.setPrint(drawableId);
    }

    @Override
    public void save() {
        canvasView.saveBitmap();
    }

    @Override
    public void load() {
        canvasView.loadBitmap();
    }

    @Override
    public void applyFilter(CanvasView.Filter filter) {
        canvasView.applyFilter(filter);
    }
}
