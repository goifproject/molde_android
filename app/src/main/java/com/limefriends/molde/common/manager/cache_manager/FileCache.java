package com.limefriends.molde.common.manager.cache_manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface FileCache {

    FileEntry get(String key);

    void put(String key, ByteProvider provider) throws IOException;

    void put(String key, InputStream is) throws IOException;

    void put(String key, File sourceFile, boolean move) throws IOException;

    void remove(String key);

    void clear();
}
