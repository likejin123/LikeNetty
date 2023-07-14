package com.likejin.nio;



import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Author 李柯锦
 * @Date 2023/7/13 16:53
 * @Description 利用channel完成文件的读取
 */
public class NIOFileChannel02 {
    public static void main(String[] args)  throws Exception{
        //创建文件输入流
        File file = new File("d:\\file01.txt");
        FileInputStream inputStream = new FileInputStream(file);

        //输入流获取文件channel
        FileChannel fileChannel = inputStream.getChannel();

        //创建缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate((int)file.length());

        //将通道的数据读入到buffer中
        fileChannel.read(byteBuffer);

        //将字节转换成字符串
        System.out.println(new String(byteBuffer.array()));
        inputStream.close();
    }
}
