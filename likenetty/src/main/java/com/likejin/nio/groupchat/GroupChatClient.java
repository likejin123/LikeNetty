package com.likejin.nio.groupchat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @Author 李柯锦
 * @Date 2023/7/14 9:35
 * @Description
 */
public class GroupChatClient {

    //定义相关的属性
    private final String HOST = "127.0.0.1"; //服务器IP
    private final int PORT = 6667;//服务器端口

    private Selector selector;
    private SocketChannel socketChannel;
    private String username;

    //完成初始化工作
    public GroupChatClient(){
        try{
            //得到选择器
            selector = Selector.open();
            //获取连接
            socketChannel = socketChannel.open(new InetSocketAddress("127.0.0.1",PORT));
            //设置非阻塞
            socketChannel.configureBlocking(false);
            //将channel注册到selector
            socketChannel.register(selector, SelectionKey.OP_READ);
            //得到username
            username = socketChannel.getLocalAddress().toString().substring(1);
            System.out.println(username + "is ok");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //向服务器发送消息
    public void sendInfo(String info){
        info = username + "说：" + info;
        try{
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //读取从服务器端发送的消息
    public void readInfo(){
        try{
            int readChannels = selector.select();
            if(readChannels > 0){//有可用的通道
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if(key.isReadable()){
                        //得到相关的通道
                        SocketChannel sc = (SocketChannel)key.channel();
                        //得到一个buffer
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        //读取
                        sc.read(buffer);
                        //把缓冲区的数据转成字符串
                        String msg = new String(buffer.array());
                        System.out.println(msg.trim());
                    }else{
                        System.out.println("没有可用的通道...");
                    }
                }
                iterator.remove();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //启动客户端
        GroupChatClient chatClient = new GroupChatClient();

        //启动线程,每隔三秒，读取从服务器端发送的数据
        new Thread(){
            public void run(){
                while(true){
                    chatClient.readInfo();
                    try {
                        Thread.currentThread().sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        //发送数据给服务端
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNextLine()){
            String s = scanner.nextLine();
            chatClient.sendInfo(s);
        }
    }
}
