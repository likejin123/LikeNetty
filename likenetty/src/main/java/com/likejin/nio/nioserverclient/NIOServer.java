package com.likejin.nio.nioserverclient;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author 李柯锦
 * @Date 2023/7/13 20:23
 * @Description NIOserver端
 */
public class NIOServer {
    public static void main(String[] args) throws Exception {
        //创建ServerSocketChanel -> serverSocket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //得到一个Selector对象
        Selector selector = Selector.open();

        //绑定一个端口6666在服务器端监听
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        //把serverSocketChannel注册到selector 关心事件为op_accept
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //循环等待客户端连接
        while (true){

            //这里等待一秒，一秒后如果没有事件发生，返回
            if(selector.select(1000) == 0){//没有事件发生
                System.out.println("服务器等待了一秒，无连接");
                continue;
            }
            //如果返回的不是0,获取到相关的SelectionKey集合，
            // 1.如果返回的大于0，表示已经获取到关注的事件
            // 2. selector.selectedKeys() 返回关注事件的集合
            ///3. 通过selectionKeys反向获取通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            //遍历Set<SelectionKey> ，使用迭代器
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while(keyIterator.hasNext()){
                //获取到SelectionKey
                SelectionKey key = keyIterator.next();
                //根据key对应的通道发生的事件做响应的处理
                if(key.isAcceptable()){//如果是OP_ACCEPt 有新的客户端连接
                    //给该客户端生成一个SocketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("客户端连接成功，生成了一个socketChannel" + socketChannel.hashCode());
                    //将SocketChannel设置为非阻塞
                    socketChannel.configureBlocking(false);
                    //将当前socketChannel注册到selector中,关注事件为OP_READ，
                    // 同时给socketChannel关联一个buffer
                    socketChannel.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }
                if(key.isReadable()){//发生了OP_READ
                    //通过key反向获取到对应的channel
                    SocketChannel channel =(SocketChannel) key.channel();
                    //获取到该channel关联的buffer
                    ByteBuffer buffer = (ByteBuffer)key.attachment();
                    //将通道数据读到buffer中,如果buffer已满，立即返回（如果是阻塞，则会等待）
                    channel.read(buffer);
                    System.out.println("客户端发送" + new String(buffer.array()));
                }
                //手动从集合中移除当前的selectionKey,防止重复操作
                keyIterator.remove();
            }
        }
    }
}
