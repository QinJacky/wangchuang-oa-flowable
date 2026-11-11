package com.wangchuan.admin.storage.write;


import com.wangchuan.admin.storage.write.domain.WriteFile;

import java.io.InputStream;

public abstract class Writer {
    public abstract void write(InputStream inputStream, WriteFile writeFile);
}
