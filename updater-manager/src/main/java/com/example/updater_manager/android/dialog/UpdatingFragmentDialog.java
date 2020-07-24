package com.example.updater_manager.android.dialog;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.updater_manager.R;
import com.example.updater_manager.android.widgets.NumberProgressBar;


public class UpdatingFragmentDialog extends DialogFragment {

    public NumberProgressBar getNumberProgressBar() {
        return numberProgressBar;
    }

    private NumberProgressBar numberProgressBar;

    public UpdatingFragmentDialog() {

    }

    View.OnClickListener mOnUpdateListener;

    public UpdatingFragmentDialog setOnUpdateListener(View.OnClickListener mOnUpdateListener) {
        this.mOnUpdateListener = mOnUpdateListener;
        return this;
    }

    public UpdatingFragmentDialog setCancel(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    boolean cancelable = false;

    protected int getContentLayoutId() {
        return R.layout.updating_dialog_setting;
    }

    private void initData() {
        setCancelable(cancelable);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.XUpdate_Fragment_Dialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        initDialog();
    }

    private void initDialog() {
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);
//        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                //如果是强制更新的话，就禁用返回键
//                return keyCode == KeyEvent.KEYCODE_BACK && mUpdateEntity != null && mUpdateEntity.isForce();
//            }
//        });

        Window window = getDialog().getWindow();
        if (window != null) {
//            window.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
            window.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams lp = window.getAttributes();
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            lp.width = (int) (displayMetrics.widthPixels * 0.75);
            lp.height = (int) (displayMetrics.heightPixels * 0.6);
//            lp.verticalMargin=displayMetrics.heightPixels/2-45;
            window.setAttributes(lp);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getContentLayoutId(), container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initView(View rootView) {
        numberProgressBar = rootView.findViewById(R.id.npb_progress);
        numberProgressBar.setMax(100);
        numberProgressBar.setVisibility(View.VISIBLE);

    }

    private void setProgress(int progress) {
        numberProgressBar.setProgress(progress);
    }

}
