package com.limefriends.molde.common.helper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class MediaQueryHelper {

    ContentResolver mContentResolver;

    public MediaQueryHelper(ContentResolver mContentResolver) {
        this.mContentResolver = mContentResolver;
    }


    /**
     * Check which column exist
     * @param absPath
     */
    public void checkoutUriColumnByPath(String absPath) {

        Cursor cursor = mContentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , null
                , MediaStore.Images.Media.DATA + "=? "
                , new String[]{absPath}, null);

        if (cursor != null && cursor.moveToFirst()) {

            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String name = cursor.getColumnName(i);

                switch (cursor.getType(i)) {
                    case Cursor.FIELD_TYPE_STRING:
                        String stringData = cursor.getString(cursor.getColumnIndex(name));
                        Log.e("호출확인", name + " : " + stringData);
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        float floatData = cursor.getFloat(cursor.getColumnIndex(name));
                        Log.e("호출확인", name + " : " + floatData);
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        int intData = cursor.getInt(cursor.getColumnIndex(name));
                        Log.e("호출확인", name + " : " + intData);
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        byte[] booleanData = cursor.getBlob(cursor.getColumnIndex(name));
                        Log.e("호출확인", name + " : " + booleanData);
                        break;
                    case Cursor.FIELD_TYPE_NULL:
                        Log.e("호출확인", name + " : null");
                        break;
                }
            }
        }
    }

    private Uri getContentUriByPath(String absPath) {

        Cursor cursor = mContentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , null
                , MediaStore.Images.Media.DATA + "=? "
                , new String[]{absPath}, null);

        if (cursor != null && cursor.moveToFirst()) {

            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));

            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String name = cursor.getColumnName(i);

                switch (cursor.getType(i)) {
                    case Cursor.FIELD_TYPE_STRING:
                        String stringData = cursor.getString(cursor.getColumnIndex(name));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        float floatData = cursor.getFloat(cursor.getColumnIndex(name));
                        Log.e("호출확인", name + " : " + floatData);
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        int intData = cursor.getInt(cursor.getColumnIndex(name));
                        Log.e("호출확인", name + " : " + intData);
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        byte[] booleanData = cursor.getBlob(cursor.getColumnIndex(name));
                        Log.e("호출확인", name + " : " + booleanData);
                        break;
                    case Cursor.FIELD_TYPE_NULL:
                        Log.e("호출확인", name + " : null");
                        break;
                }
            }

            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(id));
        }
        else if (!absPath.isEmpty()) {

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, absPath);
            values.put(MediaStore.Images.Media.ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            return mContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
        else {
            return null;
        }
    }

}
