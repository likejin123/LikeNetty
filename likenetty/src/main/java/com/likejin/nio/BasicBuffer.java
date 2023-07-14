package com.likejin.nio;

import java.nio.IntBuffer;

/**
 * @Author 李柯锦
 * @Date 2023/7/13 16:07
 * @Description
 */
public class BasicBuffer {

    /*
     * @Description 举例说明buffer的使用
     * @param args
     * @return void
     **/
    public static void main(String[] args) {
        //简单说明
        //创建一个buffer
        IntBuffer intBuffer = IntBuffer.allocate(5);

        //向Buffer中存放数据
        intBuffer.put(10);
        intBuffer.put(11);
        intBuffer.put(12);
        intBuffer.put(13);
        intBuffer.put(14);

        //从buffer中读取数据

        //将buffer转换，读写切换。
        intBuffer.flip();

        while(intBuffer.hasRemaining()){
            System.out.println(intBuffer.get());
        }
    }
}
