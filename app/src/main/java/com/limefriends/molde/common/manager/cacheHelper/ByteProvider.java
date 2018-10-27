package com.limefriends.molde.common.manager.cacheHelper;

import java.io.IOException;
import java.io.OutputStream;

public interface ByteProvider {

    void writeTo(OutputStream os) throws IOException;
}
