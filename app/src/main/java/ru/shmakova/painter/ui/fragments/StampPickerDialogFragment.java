package ru.shmakova.painter.ui.fragments;

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

public class StampPickerDialogFragment extends AppCompatDialogFragment {
    private Unbinder unbinder;

    public static StampPickerDialogFragment newInstance() {
        return new StampPickerDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stamp_picker, container, false);
        unbinder = ButterKnife.bind(this, view);
        getDialog().setTitle(getResources().getString(R.string.stamp_pick));
        return view;
    }

    @OnClick({R.id.sticker_1, R.id.sticker_2, R.id.sticker_3})
    public void onStickerOneClick(View v) {
        sendBackResult(v.getId());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public void sendBackResult(int stamp) {
        StampPickerDialogListener listener = (StampPickerDialogListener) getTargetFragment();
        listener.onStampPick(stamp);
        dismiss();
    }


    public interface StampPickerDialogListener {
        void onStampPick(int stamp);
    }

}