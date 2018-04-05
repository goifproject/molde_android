package com.limefriends.molde.menu_map.cacheManager;

import java.io.IOException;
import java.io.OutputStream;

public interface ByteProvider {
    void writeTo(OutputStream os) throws IOException;
}
