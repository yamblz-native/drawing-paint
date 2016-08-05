package ru.yandex.yamblz.ui.fragments;

import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.views.CanvasView;

/**
 * Created by root on 8/5/16.
 */
public class MenuFragment extends DialogFragment {

    private static final int REQUEST_CODE = 1;
    @BindView(R.id.colors) RadioGroup colors;
    @BindView(R.id.tools) RadioGroup tools;
    @BindView(R.id.prints) RadioGroup prints;
    @BindView(R.id.filters) RadioGroup filters;
    @BindView(R.id.save) Button save;
    @BindView(R.id.load) Button load;

    private Unbinder viewBinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_menu, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(R.string.draw);
        viewBinder = ButterKnife.bind(this, view);

        colors.setOnCheckedChangeListener(colorsListener());
        tools.setOnCheckedChangeListener(toolsListener());
        prints.setOnCheckedChangeListener(printsListener());
        filters.setOnCheckedChangeListener(filtersListener());

        save.setOnClickListener((v) -> {
            ((CanvasFragment) getTargetFragment()).save();
            getDialog().hide();
        });
        load.setOnClickListener((v) -> {
            ((CanvasFragment) getTargetFragment()).load();
            getDialog().hide();
        });
    }

    public static MenuFragment newInstance(CanvasFragment targetFragment) {
        MenuFragment instance = new MenuFragment();
        instance.setTargetFragment((Fragment) targetFragment, REQUEST_CODE);
        return instance;
    }

    private RadioGroup.OnCheckedChangeListener colorsListener() {
        return (group, checkedId) -> {

            // get color from shadowColor property of radioButton that we set in xml as info
            int color = ((RadioButton) group.findViewById(checkedId)).getShadowColor();;
            ((CanvasFragment) getTargetFragment()).setColor(color);
        };
    }

    private RadioGroup.OnCheckedChangeListener filtersListener() {
        return (group, checkedId) -> {
            CanvasView.Filter filter = CanvasView.Filter.NO_FILTER;
            switch (checkedId) {
                case R.id.original:
                    break;
                case R.id.black_and_white:
                    filter = CanvasView.Filter.BLACK_AND_WHITE;
                    break;
                case R.id.sepia:
                    filter = CanvasView.Filter.SEPIA;
                    break;
            }
            ((CanvasFragment) getTargetFragment()).applyFilter(filter);
        };
    }

    private RadioGroup.OnCheckedChangeListener toolsListener() {
        return (group, checkedId) -> {
            uncheckButtons(prints);
            CanvasView.Tool tool = CanvasView.Tool.BRUSH;
            switch (checkedId) {
                case R.id.brush:
                    tool = CanvasView.Tool.BRUSH;
                    break;
                case R.id.text:
                    tool = CanvasView.Tool.TEXT;
                    break;
            }
            ((CanvasFragment) getTargetFragment()).setTool(tool);
        };
    }

    private RadioGroup.OnCheckedChangeListener printsListener() {
        return (group, checkedId) -> {
            uncheckButtons(tools);
            switch (checkedId) {
                case R.id.cat:
                    ((CanvasFragment) getTargetFragment()).setPrint(R.drawable.cat);
                    break;
                case R.id.sun:
                    ((CanvasFragment) getTargetFragment()).setPrint(R.drawable.sun);
                    break;
            }
        };
    }

    private void uncheckButtons(RadioGroup group) {
        for (int i = 0; i < group.getChildCount(); ++i) {
            ((RadioButton) group.getChildAt(i)).setChecked(false);
        }
    }

    @Override
    public void onDestroyView() {
        if (viewBinder != null) {
            viewBinder.unbind();
        }
        super.onDestroyView();
    }

    public interface CanvasFragment {
        void setColor(int color);
        void setTool(CanvasView.Tool tool);
        void setPrint(int drawableId);
        void save();
        void load();
        void applyFilter(CanvasView.Filter filter);
    }

}
