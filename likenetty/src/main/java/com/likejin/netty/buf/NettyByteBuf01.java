package com.likejin.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @Author 李柯锦
 * @Date 2023/7/14 18:50
 * @Description netty框架提供buffer的基本使用
 */
public class NettyByteBuf01 {

    public static void main(String[] args) {

        //1.创建对象，该对象包含一个数组arr，是一个byte[10]
        //2.在netty的buffer中，不需要使用flip进行翻转
        //  底层维护了readindex（下一个读取的位置） 和 writerindex（下一个写入的位置）
        //3.通过readerindex 和 writerindex 和 capacity 将数据分成三个区域
        // 0 - readerIndex 已经读取的区域
        // readerindex - writerindex 可读的区域
        // writerindex - capactiy 可写的区域
        ByteBuf buffer = Unpooled.buffer(10);

        for(int i = 0 ; i < 10; i++){
            buffer.writeByte(i);
        }


        //输出
        for(int i = 0; i < buffer.capacity(); i++){
            System.out.println(buffer.readByte());
        }
    }
}
