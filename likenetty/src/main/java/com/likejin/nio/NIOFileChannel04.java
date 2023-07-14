package com.likejin.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * @Author 李柯锦
 * @Date 2023/7/13 17:17
 * @Description 用channel的transformto完成文件的复制
 */
public class NIOFileChannel04 {

    public static void main(String[] args) throws Exception {
        File file = new File("d:\\file01.txt");
        FileInputStream inputStream = new FileInputStream(file);

        FileOutputStream outputStream = new FileOutputStream("d:\\file03.txt");


        //获取各个流对应的filechannel

        FileChannel source = inputStream.getChannel();
        FileChannel target = outputStream.getChannel();

        //使用transferFrom完成考别
        target.transferFrom(source,0,source.size());
        //关闭相关的流

        source.close();
        target.close();
        inputStream.close();
        outputStream.close();
    }
}
