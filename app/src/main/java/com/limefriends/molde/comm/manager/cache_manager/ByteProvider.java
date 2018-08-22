package com.limefriends.molde.comm.manager.cache_manager;

import java.io.IOException;
import java.io.OutputStream;

public interface ByteProvider {
    void writeTo(OutputStream os) throws IOException;
}
