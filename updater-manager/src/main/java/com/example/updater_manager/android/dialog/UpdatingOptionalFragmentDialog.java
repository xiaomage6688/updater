package com.example.updater_manager.android.dialog;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.updater_manager.R;


public class UpdatingOptionalFragmentDialog extends DialogFragment {

    private TextView versionNameTv;
    private TextView contentViewTv;
    private TextView tvLeft;
    private TextView tvRight;

    private String versionNameAndApkSize;
    private String content = "";

    public UpdatingOptionalFragmentDialog() {

    }

    public void setVersionNameAndApkSize(String versionNameAndApkSize) {
        this.versionNameAndApkSize = versionNameAndApkSize;
    }

    View.OnClickListener mOnUpdateListener;

    public UpdatingOptionalFragmentDialog setContent(String content) {
        this.content = content;
        return this;
    }

    public UpdatingOptionalFragmentDialog setOnUpdateListener(View.OnClickListener mOnUpdateListener) {
        this.mOnUpdateListener = mOnUpdateListener;
        return this;
    }

    public UpdatingOptionalFragmentDialog setCancel(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    boolean cancelable = true;

    protected int getContentLayoutId() {
        return R.layout.updating_optional_dialog_setting;
    }

    private void initData() {
//        setCancelable(cancelable);
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
        Window window = getDialog().getWindow();
        if (window != null) {
//            window.setGravity(Gravity.NO_GRAVITY | Gravity.CENTER_HORIZONTAL);
            window.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams lp = window.getAttributes();
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            lp.width = (int) (displayMetrics.widthPixels * 0.8);
            lp.height = (int) (displayMetrics.heightPixels * 0.6);
//            lp.verticalMargin=displayMetrics.heightPixels/2;
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
        versionNameTv = rootView.findViewById(R.id.version_name);
        contentViewTv = rootView.findViewById(R.id.content);
        tvLeft = rootView.findViewById(R.id.tvLeft);
        tvRight = rootView.findViewById(R.id.tvRight);

        versionNameTv.setText(versionNameAndApkSize);
        contentViewTv.setText(content);
        tvLeft.setOnClickListener((v) -> dismiss());
        tvRight.setOnClickListener(mOnUpdateListener);

    }

}
