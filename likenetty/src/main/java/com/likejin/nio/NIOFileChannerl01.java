package com.likejin.nio;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Author 李柯锦
 * @Date 2023/7/13 16:40
 * @Description 利用channel完成文件的保存
 */
public class NIOFileChannerl01 {

    public static void main(String[] args) throws Exception {

        String str = "hello";

        //创建一个输出流 - > channel
        FileOutputStream outputStream = new FileOutputStream("d:\\file01.txt");

        //通过输出流获取对应的文件channel
        //这个filechannel是真实类型为FileChannelImpl
        FileChannel fileChannel = outputStream.getChannel();

        //创建一个缓冲区 ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        //将String放入到byteBuffer
        byteBuffer.put(str.getBytes());

        //对byteBuffer进行翻转
        byteBuffer.flip();

        //把缓冲区的数据写入到channel中
        fileChannel.write(byteBuffer);

        outputStream.close();


    }
}
