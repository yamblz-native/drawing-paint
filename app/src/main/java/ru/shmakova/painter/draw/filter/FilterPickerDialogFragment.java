package ru.shmakova.painter.draw.filter;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.shmakova.painter.R;

/**
 * Created by shmakova on 02.08.16.
 */

public class FilterPickerDialogFragment extends AppCompatDialogFragment {
    private Unbinder unbinder;

    public static FilterPickerDialogFragment newInstance() {
        return new FilterPickerDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_picker, container, false);
        unbinder = ButterKnife.bind(this, view);
        getDialog().setTitle(getResources().getString(R.string.filter_pick));
        return view;
    }

    @OnClick({R.id.gray_scale_btn, R.id.negative_btn})
    public void onFilterClick(View v) {
        sendBackResult(v.getId());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public void sendBackResult(int filter) {
        FilterPickerDialogListener listener = (FilterPickerDialogListener) getTargetFragment();
        listener.onFilterPick(filter);
        dismiss();
    }


    public interface FilterPickerDialogListener {
        void onFilterPick(int filter);
    }

}