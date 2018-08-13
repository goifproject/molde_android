package com.limefriends.molde.ui.menu_map.cache_manager;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Cache {
    private Context context;
    private final String cacheFileName = "mapInfoSearchHistory.txt";

    public Cache(Context context) {
        this.context = context;
    }

    public File getCacheDir(Context context) {
        File cacheDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(Environment.getExternalStorageDirectory(), "moldeCache");
            if (!cacheDir.isDirectory()) {
                cacheDir.mkdirs();
            }
        }
        if (!cacheDir.isDirectory()) {
            cacheDir = context.getCacheDir();
        }
        return cacheDir;
    }

    public void write(String cache) throws IOException {
        File cacheDir = getCacheDir(context);
        File cacheFile = new File(cacheDir, cacheFileName);
        if (!cacheFile.exists()) cacheFile.createNewFile();
        FileWriter fileWriter = new FileWriter(cacheFile);
        fileWriter.write(cache);
        fileWriter.flush();
        fileWriter.close();
    }

    public String read() throws IOException {
        File cacheDir = getCacheDir(context);
        File cacheFile = new File(cacheDir, cacheFileName);
        if (!cacheFile.exists()) cacheFile.createNewFile();
        FileInputStream inputStream = new FileInputStream(cacheFile);
        Scanner s = new Scanner(inputStream);
        String text = "";
        while (s.hasNext()) {
            text += s.nextLine();
        }
        inputStream.close();
        return text;
    }

    public boolean delete() throws IOException {
        File cacheDir = getCacheDir(context);
        File file = new File(cacheDir, cacheFileName);
        file.delete();
        return cacheDir.delete();
    }

}