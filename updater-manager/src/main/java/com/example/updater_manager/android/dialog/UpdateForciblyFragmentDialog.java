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

public class UpdateForciblyFragmentDialog extends DialogFragment {

    private TextView contentView;

    public UpdateForciblyFragmentDialog() {

    }

    View.OnClickListener mOnUpdateListener;

    public UpdateForciblyFragmentDialog setContent(String content) {
        this.content = content;
        return this;
    }

    public UpdateForciblyFragmentDialog setOnUpdateListener(View.OnClickListener mOnUpdateListener) {
        this.mOnUpdateListener = mOnUpdateListener;
        return this;
    }

    String content = "";

    public UpdateForciblyFragmentDialog setCancel(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    boolean cancelable = false;

    protected int getContentLayoutId() {
        return R.layout.update_forcibly_dialog_setting;
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
            window.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams lp = window.getAttributes();
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            lp.width = (int) (displayMetrics.widthPixels * 0.75);
            lp.height = (int) (displayMetrics.heightPixels * 0.6);
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
        contentView = rootView.findViewById(R.id.content);
        contentView.setText(content);
        rootView.findViewById(R.id.update).setOnClickListener(mOnUpdateListener);

    }
}
