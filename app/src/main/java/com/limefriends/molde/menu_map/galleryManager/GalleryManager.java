package com.limefriends.molde.menu_map.galleryManager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
public class GalleryManager {


    private Context mContext;

    public GalleryManager(Context context) {
        mContext = context;
    }


    /**
     * 갤러리 이미지 반환
     */
    public List<PhotoVO> getAllPhotoPathList() {

        ArrayList<PhotoVO> photoList = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.DATE_ADDED
        };

        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);

        int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {

            PhotoVO photoVO = new PhotoVO(cursor.getString(columnIndexData),false);
            photoList.add(photoVO);
        }

        cursor.close();

        return photoList;
    }


    /**
     * 날짜별 갤러리 이미지 반환
     */
    public List<PhotoVO> getDatePhotoPathList(int year, int month, int day) {

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(year, month, day, 0, 0);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(year, month, day, 24, 0);

        String startTitme = String.valueOf(startCalendar.getTimeInMillis()).substring(0, 10);
        String endTitme = String.valueOf(endCalendar.getTimeInMillis()).substring(0, 10);

        ArrayList<PhotoVO> photoList = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED
        };

        String selection = MediaStore.Images.Media.DATE_ADDED + " >= " + startTitme + " AND "
                         + MediaStore.Images.Media.DATE_ADDED + " <= " + endTitme;

        Cursor cursor = mContext.getContentResolver().query(uri, projection, selection, null, null);

        int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {

            PhotoVO photoVO = new PhotoVO(cursor.getString(columnIndexData),false);
            photoList.add(photoVO);
        }
        cursor.close();
        return photoList;
    }


}
