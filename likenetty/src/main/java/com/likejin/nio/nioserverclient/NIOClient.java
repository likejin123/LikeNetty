package com.likejin.nio.nioserverclient;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @Author 李柯锦
 * @Date 2023/7/13 20:39
 * @Description
 */
public class NIOClient {

    public static void main(String[] args) throws Exception {
        //得到一个网络通道
        SocketChannel socketChannel = SocketChannel.open();

        //设置非阻塞模式
        socketChannel.configureBlocking(false);

        //提供服务端的IP和端口
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);

        //连接服务器
        if(!socketChannel.connect(inetSocketAddress)){
            while(!socketChannel.finishConnect()){
                System.out.println("因为连接需要时间，客户端不会阻塞，可以做其他工作");
            }
        }

        //如果连接成功就发送数据
        String str = "hello , NIO";

        //产生一个字节数组到buffer中取
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());

        //发送数据,将buffer数据写入channel
        socketChannel.write(buffer);
        System.in.read();

    }
}
