package ru.shmakova.painter.draw.filter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.OnClick;
import ru.shmakova.painter.R;
import ru.shmakova.painter.app.App;
import ru.shmakova.painter.screen.BaseDialogFragment;
import rx.Observable;
import rx.subjects.PublishSubject;

public class FilterPickerDialogFragment extends BaseDialogFragment implements FilterView {
    @NonNull
    private final PublishSubject<Integer> submitClicks = PublishSubject.create();

    @Inject
    FilterPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter_picker, container, false);
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    private void injectDependencies() {
        App.get(getContext()).applicationComponent().inject(this);
    }

    @OnClick({R.id.gray_scale_btn, R.id.negative_btn})
    public void onFilterClick(View v) {
        submitClicks.onNext(v.getId());
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.bindView(this);
    }

    @Override
    public void onDestroyView() {
        presenter.unbindView(this);
        super.onDestroyView();
    }

    @Override
    public Observable<Integer> submitClicks() {
        return submitClicks;
    }

    @Override
    public void sendBackResult(int filter) {
        FilterPickerDialogListener listener = (FilterPickerDialogListener) getTargetFragment();
        listener.onFilterPick(filter);
    }

    @Override
    public void dismissDialog() {
        dismiss();
    }


    public interface FilterPickerDialogListener {
        void onFilterPick(int filter);
    }
}
