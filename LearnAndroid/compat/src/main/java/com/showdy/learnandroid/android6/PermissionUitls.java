package com.showdy.learnandroid.android6;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by <b>Showdy</b> on 2020/10/31 19:38
 */
public class PermissionUitls {

    /**
     * 申请权限
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestPermissions(@NonNull Activity activity, @NonNull String[] permissions, int requestCode) {
        if (!needRequestPermission(activity, permissions)) {
            return;
        }
        List<String> deniedPermissions = findDeniedPermissions(activity, permissions);
        if (!deniedPermissions.isEmpty()) {
            activity.requestPermissions(permissions, requestCode);
        }
    }

    /**
     * @return 未获得的权限
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static List<String> findDeniedPermissions(Activity activity, String... permission) {
        List<String> denyPermissions = new ArrayList<>();
        for (String value : permission) {
            if (activity.checkSelfPermission(value) != PackageManager.PERMISSION_GRANTED) {
                denyPermissions.add(value);
            }
        }
        return denyPermissions;
    }

    /**
     * Android系统6.0 往上
     */
    private static boolean versionOverM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * @return true: 需要动态获取的权限未全部获得
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private static boolean permissionNotGranted(Activity activity, String... permission) {
        for (String value : permission) {
            if (activity.checkSelfPermission(value) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return true: 所需要的权限未全部已获取，需申请权限
     */
    private static boolean needRequestPermission(Activity activity, String... permission) {
        return versionOverM() && permissionNotGranted(activity, permission);
    }




    /**
     * 申请权限后，是否所有的权限都申请成功
     *
     * @return true:所有权限申请成功
     */
    public static boolean isAllPermissionsGranted(int... permissions) {
        if (permissions == null) {
            return true;
        }
        for (int p : permissions) {
            if (p != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 权限申请统一处理信息
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void dispatchPermissionResult(@NonNull Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissions.length == 0 || grantResults.length == 0) {
            return;
        }
        for (int index = 0; index < permissions.length; index++) {
            if (grantResults[index] != PackageManager.PERMISSION_GRANTED
                    && !activity.shouldShowRequestPermissionRationale(permissions[index])) {
                //用户之前拒绝，并勾选不再提示时，在此引导用户去设置页设置权限
                jumpToSettingPage(activity);
            }
        }
    }

    /**
     * 帮跳转到该应用的设置界面，让用户手动授权
     */
    private static void jumpToSettingPage(@NonNull Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }
}
