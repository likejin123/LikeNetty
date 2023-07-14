package com.likejin.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

/**
 * @Author 李柯锦
 * @Date 2023/7/14 18:57
 * @Description
 */
public class NettyByteBuf02 {

    public static void main(String[] args) {

        //创建bytebuf
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello,world", CharsetUtil.UTF_8);

        //使用相关的方法
        if(byteBuf.hasArray()){// true
            byte[] content = byteBuf.array();

            //转换成字符串
            System.out.println(new String(content, Charset.forName("utf-8")));



        }
    }
}
