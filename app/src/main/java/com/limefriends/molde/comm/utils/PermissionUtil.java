package com.limefriends.molde.comm.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;

public class PermissionUtil {

    // TODO

    private String[] permissions = {};
    public static int REQ_CODE = 999;
//    private static PermissionUtil permission;

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

    /**
     * 데이터베이스 연결이 아니고, 가장 먼저 하는 일이기 때문에 싱글턴으로 해도 메모리 누수가 생기지 않는다.
     * 라고 생각했으나 activity 와 fragment 전체를 넘겨받아 static 으로 메모리에 올려두니 어플리케이션이
     * 인스턴스에서 사라지기 전까지 메모리에 남아있어 계속해서 알 수 없는 오류를 발생시킴.
     * 이번 경우는 getContext 가 왜 자꾸 null 이 뜨는 것인지 전혀 이해할 수 없었음
     */
//    public static PermissionUtil getInstance(Activity activity, PermissionCallback callback){
//        if(permission == null){
//            permission = new PermissionUtil(activity, callback);
//        }
//        return permission;
//    }

    public void checkPermission(String[] permissions) {
        this.permissions = permissions;
        checkVersion();
    }

    private void checkVersion(){
        // 버전이 마시멜로 미만인 경우는 패스
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
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
        for(String perm : permissions){
            // 만약 원하는 퍼미션이 하나라도 허용이 안 되었다면 false로 전환
            if(activity.checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED){
                isAllGranted = false;
            }
        }
        // 만약 전부 허용이 되었다면 다음 액티비티로 넘어간다.
        if(isAllGranted){
            callback.onPermissionGranted();
            // 허용되지 않는 것이 있다면 시스템에 권한신청한다.
        } else {
            activity.requestPermissions(permissions, REQ_CODE);
        }
    }

    /**
     * 시스템 권한체크가 끝난 후 호출
     */
    public void onResult(int[] grantResults){
        boolean isAllGranted = true;
        for(int granted : grantResults){
            if(granted != PackageManager.PERMISSION_GRANTED){
                isAllGranted = false;
            }
        }
        // 허용되면 init()으로 원하는 함수를 실행하고
        if(isAllGranted){
            callback.onPermissionGranted();
            // 허용되지 않는 것이 있다면 시스템에 권한신청한다.
        } else {
            callback.onPermissionDenied();
        }

//
    }

    /**
     * settings 페이지로 넘어감
     */
    // TODO Dialog 유틸로 뺴냄
//    private void showAskAgainDialog() {
//        AlertDialog dialog = new AlertDialog.Builder(activity)
//                .setTitle("권한 설정 필요")
//                .setMessage("현재 기능을 사용하기 위해서는 해당 권한 설정이 필요합니다. 설정 페이지로 넘어가시겠습니까?")
//                .setPositiveButton("예", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        goSettings();
//                    }
//                })
//                .setNegativeButton("아니오", null)
//                .create();
//        dialog.show();
//    }
//
//
//    private void goSettings() {
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS,
//                Uri.fromParts("package", activity.getPackageName(), null));
//        activity.startActivity(intent);
//    }
}
