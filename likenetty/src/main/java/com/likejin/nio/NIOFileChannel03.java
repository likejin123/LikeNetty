package com.likejin.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Author 李柯锦
 * @Date 2023/7/13 17:01
 * @Description 一个Buffer完成文件的复制
 */
public class NIOFileChannel03 {

    public static void main(String[] args) throws Exception{
        File file = new File("d:\\file01.txt");
        FileInputStream inputStream = new FileInputStream(file);

        FileChannel channel01 = inputStream.getChannel();

        FileOutputStream outputStream = new FileOutputStream("d:\\file02.txt");

        FileChannel channel02 = outputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(512);

        //循环读取
        while (true){

            //这里有一个重要的操作，一定不能忘了，将标志位重置
            //将关键的属性重置
            byteBuffer.clear();
            //如果不加这个。。那么read会为循环为0.。
            //即当最后执行channel02.write(byteBuffer);之后
            // byteBuffer的position == limit时，此时由于byteBuffer是满的，无法读入，read值一直为0

            //只有加上重置状态
            //此时position归0，可以加，才能读入

            //读取channel到buffer
            int read = channel01.read(byteBuffer);
            if(read == -1){
                //表示读取完毕
                break;
            }
            byteBuffer.flip();
            //buffer写入channel
            channel02.write(byteBuffer);
        }

        //关闭相关的流
        inputStream.close();
        outputStream.close();

    }
}
