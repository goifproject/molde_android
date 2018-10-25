package com.limefriends.molde.common.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

public class PermissionUtil {

    private String[] permissions = {};
    public static int REQ_CODE = 999;

    private PermissionCallback callback;

    private Activity activity;

    public interface PermissionCallback {
        void onPermissionGranted();

        void onPermissionDenied();
    }

    public PermissionUtil(Activity activity, PermissionCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public void checkPermission(String[] permissions) {
        this.permissions = permissions;
        checkVersion();
    }

    private void checkVersion() {
        // 버전이 마시멜로 미만인 경우는 패스
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            callback.onPermissionGranted();
            // 이상인 경우는 일단 허용이 된 퍼미션이 무엇인지 체크한다.
        } else {
            checkAlreadyGrantedPermission();
        }
    }

    /**
     * 이미 체크된 퍼미션이 있는지 확인하고, 체크되지 않았다면 시스템에 onRequestPermission()으로 권한을 요청한다.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkAlreadyGrantedPermission() {
        boolean isAllGranted = true;
        for (String perm : permissions) {
            // 만약 원하는 퍼미션이 하나라도 허용이 안 되었다면 false로 전환
            if (activity.checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                isAllGranted = false;
            }
        }
        // 만약 전부 허용이 되었다면 다음 액티비티로 넘어간다.
        if (isAllGranted) {
            callback.onPermissionGranted();
            // 허용되지 않는 것이 있다면 시스템에 권한신청한다.
        } else {
            activity.requestPermissions(permissions, REQ_CODE);
        }
    }

    /**
     * 시스템 권한체크가 끝난 후 호출
     */
    public void onResult(int[] grantResults) {
        boolean isAllGranted = true;
        for (int granted : grantResults) {
            if (granted != PackageManager.PERMISSION_GRANTED) {
                isAllGranted = false;
            }
        }
        // 허용되면 init()으로 원하는 함수를 실행하고
        if (isAllGranted) {
            callback.onPermissionGranted();
            // 허용되지 않는 것이 있다면 시스템에 권한신청한다.
        } else {
            callback.onPermissionDenied();
        }
        }

}
