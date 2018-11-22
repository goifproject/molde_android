package com.limefriends.molde.common.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.support.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapHelper {

    public interface BitmapSavedListener {

        void onBitmapSavedListener();
    }

    /**
     * 비트맵 관련 라이브러리
     */
    public static final String TAG = BitmapHelper.class.getSimpleName();

    /**
     * 비트맵을 별도 스레드에서 파일로 저장한다.
     *
     * @param handler 결과를 알려줄 핸들러
     * @param file    파일 객체
     * @param seq  몇번째 이미지인지 확인
     */
    public void saveBitmapToFileThread(final Handler handler, final File file, final int seq) {
        new Thread() {
            @Override
            public void run() {
                saveBitmapToFile(file);
                Message message = Message.obtain();
                message.obj = file;
                message.what = seq;
                handler.sendMessage(message);
            }
        }.start();
    }

    public void saveBitmapToFileThread(final BitmapSavedListener listener, final File file,
                                       final Bitmap bitmap) {
        new Thread() {
            @Override
            public void run() {
                saveBitmapToFile(file, bitmap);
                listener.onBitmapSavedListener();
            }
        }.start();
    }

    /**
     * 비트맵을 파일에 저장한다.
     *
     * @param file   파일 객체
     * @param bitmap 비트맵 객체
     * @return 파일 저장 성공 여부
     */
    private boolean saveBitmapToFile(File file, Bitmap bitmap) {

        if (bitmap == null) return false;

        boolean save = false;

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            save = true;
        } catch (Exception e) {
            save = false;
        } finally {
            try {
                out.close();
            } catch (Exception e) {
            }
        }
        return save;
    }

    private boolean saveBitmapToFile(File file) {

        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());

        if (bitmap == null) return false;

        boolean save = false;

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            save = true;
        } catch (Exception e) {
            save = false;
        } finally {
            try {
                out.close();
            } catch (Exception e) {
            }
        }
        return save;
    }

    public Bitmap resize(File uri, int resize) {
        Bitmap resizedBitmap = null;
        Bitmap rotatedBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        BitmapFactory.decodeFile(uri.getPath(), options);

        int width = options.outWidth;
        int height = options.outHeight;
        int sampleSize = 1;

        while (true) {
            if (width / 2 < resize || height / 2 < resize) break;
            width /= 2;
            height /= 2;
            sampleSize *= 2;
        }

        options.inSampleSize = sampleSize;

        resizedBitmap = BitmapFactory.decodeFile(uri.getPath(), options);

        rotatedBitmap = rotateBitmap(resizedBitmap, uri.getPath());

        return rotatedBitmap;
    }

    //회전방지
    private static Bitmap rotateBitmap(Bitmap bitmap, String filePath) {

        int orientation = ExifInterface.ORIENTATION_NORMAL;
        try{
            ExifInterface exifInterface = new ExifInterface(filePath);
            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Matrix matrix = new Matrix();

        switch (orientation) {
            case ExifInterface.ORIENTATION_UNDEFINED:
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        matrix.setRotate(90);
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

}
