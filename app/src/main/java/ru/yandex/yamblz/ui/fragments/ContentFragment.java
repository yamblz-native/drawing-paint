package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.shchurov.horizontalwheelview.HorizontalWheelView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.views.CanvasView;

public class ContentFragment extends BaseFragment {

    @BindView(R.id.fragment_content_wheel_view)
    HorizontalWheelView mWheelView;

    @BindView(R.id.fragment_content_canvas_view)
    CanvasView mCanvasView;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        ButterKnife.bind(this, view);

        mWheelView.setListener(new HorizontalWheelView.Listener() {
            @Override
            public void onRotationChanged(double radians) {
                mCanvasView.setPaintSize((float) Math.abs(radians) * 10);
            }
        });

        return view;
    }
}
