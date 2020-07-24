package com.example.updater_manager.android;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.fragment.app.FragmentActivity;


import com.example.updater_manager.android.dialog.UpdateForciblyFragmentDialog;
import com.example.updater_manager.android.dialog.UpdatingFragmentDialog;
import com.example.updater_manager.android.dialog.UpdatingOptionalFragmentDialog;
import com.king.app.updater.AppUpdater;
import com.king.app.updater.callback.AppUpdateCallback;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 使用实例
 * ApkUpdateManager.builder(context)
 * .setUpdateType(update_type)
 * .setApkSize(updateInfo.getApkSize())
 * .setVersion(updateInfo.getVersionName())
 * .setApkUrl(updateInfo.getDownloadUrl())
 * .setUpdateContent(updateInfo.getUpdateDetail())
 * .setUpdaterListener((context1, apkFile) -> {
 * install(context, apkFile);
 * }).start();
 */


public class ApkUpdateManager {

    public interface UpdaterListener {
        void onDownloadComplete(Context context, File apkFile);
    }

    public static class Builder {
        @UpdateType
        int type;
        protected String apkUrl;
        protected String version="";
        protected String apkSize="";
        protected String updateContent="";
        protected FragmentActivity context;
        protected Handler mainHandler;
        protected UpdaterListener updaterListener;

        protected Builder runOnUiThread(Runnable runnable) {
            mainHandler.post(runnable);
            return this;
        }

        protected Builder install(Context context, File apkFile) {
            if (updaterListener != null) {
                updaterListener.onDownloadComplete(context, apkFile);
            }
            return this;
        }

        public Builder setUpdaterListener(UpdaterListener updaterListener) {
            this.updaterListener = updaterListener;
            return this;
        }

        public Builder setUpdateContent(String updateContent) {
            this.updateContent = updateContent;
            return this;
        }

        public Builder(FragmentActivity context) {
            this.context = context;
            mainHandler = new Handler(Looper.getMainLooper());
        }

        /**
         * type 有三种可选值 SILENT(静默安装)、FORCIBLY(强制)、SELECT(选择更新)
         *
         * @param type
         * @return
         */
        public Builder setUpdateType(@UpdateType int type) {
            this.type = type;
            return this;
        }

        public Builder setApkUrl(String apkUrl) {
            this.apkUrl = apkUrl;
            return this;
        }

        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder setApkSize(String apkSize) {
            this.apkSize = apkSize;
            return this;
        }

        public void start() {
            checkParameters();
            mainHandler.post(() -> download(context, this));
        }

        private void checkParameters() {
            if (type == 0) {
                throw new IllegalArgumentException("更新的安装方式type未赋值");
            }
            if (TextUtils.isEmpty(apkUrl)) {
                throw new IllegalArgumentException("apk 下载路径不能为NULL");
            }
            if (TextUtils.isEmpty(updateContent)) {
                throw new IllegalArgumentException("更新详情不能为NULL");
            }
        }
    }

    @IntDef({SILENT, SELECT, FORCIBLY})
    @Retention(RetentionPolicy.SOURCE)
    public static @interface UpdateType {

    }

    public static final int SILENT = 3;
    public static final int SELECT = 1;
    public static final int FORCIBLY = 2;

    public static Builder builder(FragmentActivity context) {
        return new Builder(context);
    }

    private static void download(FragmentActivity context, Builder builder) {
        Log.d("ApkUpdateManager", "开始校验");
        switch (builder.type) {
            case 1:
                //静默更新
                silentInstallationUpdaterStart(context, builder);
                break;
            case 2:
                // 提示更新
                UpdatingOptionalFragmentDialog updatingOptionalFragmentDialog = new UpdatingOptionalFragmentDialog();
                updatingOptionalFragmentDialog.setContent(builder.updateContent);
                String stringBuilder = "版本" + builder.version + "  " + builder.apkSize+"MB";
                updatingOptionalFragmentDialog.setVersionNameAndApkSize(stringBuilder);
                updatingOptionalFragmentDialog.setOnUpdateListener((v) -> {
                    updatingOptionalFragmentDialog.dismiss();
                    appUpdaterStart(context, builder);
                });
                updatingOptionalFragmentDialog.showNow(context.getSupportFragmentManager(), "updatingOptionalFragmentDialog");
                break;
            default:
                // 强制
                UpdateForciblyFragmentDialog updateForciblyFragmentDialog = new UpdateForciblyFragmentDialog();
                updateForciblyFragmentDialog.setContent(builder.updateContent);
                updateForciblyFragmentDialog.show(context.getSupportFragmentManager(), "UpdateForciblyFragmentDialog");
                updateForciblyFragmentDialog.setOnUpdateListener((v) -> {
                    updateForciblyFragmentDialog.dismiss();
                    appUpdaterStart(context, builder);
                });
        }
    }

    private static void appUpdaterStart(FragmentActivity context, Builder builder) {

        UpdatingFragmentDialog UpdatingDialog = new UpdatingFragmentDialog();
        UpdatingDialog.show(context.getSupportFragmentManager(), "UpdatingFragmentDialog");

        Log.d("ApkUpdateManager", "开始下载-apkUrl"  + "-" + builder.apkUrl);


        AppUpdater appUpdater = new AppUpdater.Builder()
                .serUrl(builder.apkUrl)
                .setInstallApk(false)
                .setShowNotification(false)
                .build(context)
                .setUpdateCallback(new AppUpdateCallback() {
                    @Override
                    public void onProgress(long progress, long total, boolean isChange) {

                        Log.d("ApkUpdateManager", "progress-" + progress + "-" + isChange);
                        Log.d("ApkUpdateManager", "total-" + total);
                        int pro = (int) (progress * 100 / total);
                        builder.runOnUiThread(() -> {
                            try {
                                UpdatingDialog.getNumberProgressBar().setProgress(pro);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("ApkUpdateManager", "Exception-" + e);
                            }
                        });
                    }

                    @Override
                    public void onFinish(File file) {
                        try {
                            UpdatingDialog.getNumberProgressBar().setVisibility(View.INVISIBLE);
                            UpdatingDialog.dismiss();
                            //
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("ApkUpdateManager", "Exception-" + e);

                        }
                        Log.d("ApkUpdateManager", "onFinish-" + "-file-" + file.getAbsolutePath());

                        builder.install(context, file);
                    }
                });
        appUpdater.start();
    }

    private static void silentInstallationUpdaterStart(Context context, Builder builder) {
        AppUpdater appUpdater = new AppUpdater.Builder()
                .serUrl(builder.apkUrl)
                .setShowNotification(false)
                .setInstallApk(false)
                .build(context)
                .setUpdateCallback(new AppUpdateCallback() {
                    @Override
                    public void onProgress(long progress, long total, boolean isChange) {
                    }

                    @Override
                    public void onFinish(File file) {
                        Log.d("ApkUpdateManager", "onFinish-" + "-file-" + file.getAbsolutePath());
                        builder.install(context, file);
                    }
                });
        appUpdater.start();
    }
}
