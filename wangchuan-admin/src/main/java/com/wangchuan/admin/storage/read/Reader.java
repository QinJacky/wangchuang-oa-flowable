package com.wangchuan.admin.storage.read;


import com.wangchuan.admin.storage.read.domain.ReadFile;

public abstract class Reader {
    public abstract String read(ReadFile readFile);

    /**
     * 读取文件字节
     * @param readFile
     * @return
     */
    public abstract byte[] readBytes(ReadFile readFile);
}
