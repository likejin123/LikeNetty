package com.likejin.nio;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

/**
 * @Author 李柯锦
 * @Date 2023/7/13 17:58
 * @Description 测试通道和流的关系(流内部为通道)，即直接可以通过通道操作（等同于流）
 */
public class MyTestChannel {
    public static void main(String[] args) throws Exception {
        FileChannel fileChannel = FileChannel.open(Paths.get("d://file01.txt"));

        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        fileChannel.read(byteBuffer);

        System.out.println(new String(byteBuffer.array()));
    }
}
