package ru.yandex.yamblz.ui.fragments.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import ru.yandex.yamblz.R;

public class EditTextDialog extends DialogFragment {

    private static final String TITLE = "title";
    private static final String MESSAGE = "message";
    private static final String HINT = "hint";
    private static final String POSITIVE = "positive";
    private static final String NEGATIVE = "negative";
    private static final String NEUTRAL = "neutral";
    private static final String CANCELABLE = "cancelable";
    private static final String ID = "id";
    private static final String TEXT = "text";

    private Callbacks mCallbacks;
    private String mTitle;
    private String mMessage;
    private String mHint;
    private String mPositive;
    private String mNegative;
    private String mNeutral;
    private boolean mCancelable;
    private int mId;
    private EditText mEditText;

    public interface Callbacks {
        void onPositive(String value, int id);

        void onNegative(int id);

        void onNeutral(int id);

        void onDismiss(int id);

        void onCancel(int id);
    }


    public static class SimpleCallbacks implements Callbacks {

        @Override
        public void onPositive(String value, int id) {

        }

        @Override
        public void onNegative(int id) {

        }

        @Override
        public void onNeutral(int id) {

        }

        @Override
        public void onDismiss(int id) {

        }

        @Override
        public void onCancel(int id) {

        }
    }

    public static EditTextDialog newInstance(String title, String message, String hint, String positive,
                                             String negative, String neutral, boolean cancelable, int id) {
        EditTextDialog editTextDialog = new EditTextDialog();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(MESSAGE, message);
        bundle.putString(HINT, hint);
        bundle.putString(POSITIVE, positive);
        bundle.putString(NEGATIVE, negative);
        bundle.putString(NEUTRAL, neutral);
        bundle.putBoolean(CANCELABLE, cancelable);
        bundle.putInt(ID, id);
        editTextDialog.setArguments(bundle);
        return editTextDialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(getParentFragment() instanceof Callbacks) {
            mCallbacks = (Callbacks)getParentFragment();
        } else if(activity instanceof Callbacks) {
            mCallbacks = (Callbacks)activity;
        } else {
            throw new IllegalArgumentException("Should implement Callbacks");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TEXT, mEditText.getText().toString());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        mTitle = args.getString(TITLE);
        mMessage = args.getString(MESSAGE);
        mHint = args.getString(HINT);
        mPositive = args.getString(POSITIVE);
        mNegative = args.getString(NEGATIVE);
        mNeutral = args.getString(NEUTRAL);
        mCancelable = args.getBoolean(CANCELABLE);
        mId = args.getInt(ID);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ViewGroup textHolder = (FrameLayout)getActivity().getLayoutInflater()
                .inflate(R.layout.edit_text_dialog, null);
        mEditText = (EditText)textHolder.findViewById(R.id.text);
        builder.setView(textHolder);

        if(savedInstanceState != null) {
            mEditText.setText(savedInstanceState.getString(TEXT));
            mEditText.setSelection(mEditText.getText().length());
        }
        setCancelable(mCancelable);

        if(!TextUtils.isEmpty(mHint)) {
            mEditText.setHint(mHint);
        }

        if(!TextUtils.isEmpty(mTitle)) {
            builder.setTitle(mTitle);
        }

        if(!TextUtils.isEmpty(mMessage)) {
            builder.setMessage(mMessage);
        }

        if(!TextUtils.isEmpty(mPositive)) {
            builder.setPositiveButton(mPositive, (DialogInterface dialogInterface, int which) -> {
                mCallbacks.onPositive(mEditText.getText().toString(), mId);
            });
        }

        if(!TextUtils.isEmpty(mNegative)) {
            builder.setNegativeButton(mNegative, (DialogInterface dialogInterface, int which) -> {
                mCallbacks.onNegative(mId);
            });
        }

        if(!TextUtils.isEmpty(mNeutral)) {
            builder.setNeutralButton(mNeutral, (DialogInterface dialogInterface, int which) -> {
                mCallbacks.onNeutral(mId);
            });
        }

        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        mCallbacks.onDismiss(mId);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        mCallbacks.onCancel(mId);
    }
}
