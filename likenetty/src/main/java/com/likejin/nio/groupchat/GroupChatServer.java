package com.likejin.nio.groupchat;



import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @Author 李柯锦
 * @Date 2023/7/14 9:14
 * @Description 群聊sever端
 */
public class GroupChatServer {

    private Selector selector;

    private ServerSocketChannel listenChannel;

    private static final int PORT = 6667;

    //完成初始化工作
    public GroupChatServer(){
        try{

            //得到选择器
            selector = Selector.open();
            //得到ServerSocketChannel
            listenChannel = ServerSocketChannel.open().bind(new InetSocketAddress(PORT));
            //设置非阻塞模式
            listenChannel.configureBlocking(false);
            //将listenChannel注册到selector
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //监听消息
    public void listen(){
        try{
            //循环处理
            while(true){
                int count = selector.select();
                if(count > 0){//有事件要处理
                    //遍历得到selectionKey集合
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while(iterator.hasNext()){
                        SelectionKey key = iterator.next();

                        //监听到的accept
                        if(key.isAcceptable()){
                            SocketChannel sc = listenChannel.accept();
                            //将sc注册到selector
                            sc.configureBlocking(false);
                            sc.register(selector,SelectionKey.OP_READ);
                            //给出提示
                            System.out.println(sc.getRemoteAddress() + "上线了");
                        }

                        //监听到读的状态
                        if(key.isReadable()){
                            //处理读（专门方法）
                            readData(key);
                        }
                        //删除处理完成的key
                        iterator.remove();

                    }
                }else{
//                    System.out.println("等待....");
                }



            }
        }catch (Exception e){

        }finally {

        }
    }

    //读取客户端消息
    private void readData(SelectionKey key){
        //定义一个socketChannel
        SocketChannel channel = null;
        try{
            //取到关联的channel
            channel = (SocketChannel) key.channel();
            //创建buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            int count =  channel.read(buffer);
            //根据count值来读取
            if(count > 0){
                //把缓冲区的数据转换成字符串
                String msg = new String(buffer.array());
                System.out.println("from 客户端" + msg);

                //向其他客户端转发消息（排除自己），专门方法处理
                sendInfoToOtherClients(msg,channel);
            }

        }catch (Exception e){
            try {
                System.out.println(channel.getRemoteAddress() + "离线了");
                //取消注册
                key.cancel();
                //关闭通道
                channel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }


    //转发消息给其他的客户（通道）
    private void sendInfoToOtherClients(String msg,SocketChannel selfChannel) throws IOException {
        System.out.println("服务器转发消息...");
        //遍历所有注册到selector上的SoketChannel，并排除self
        for(SelectionKey key : selector.keys()){

            //通过key取出对应的channel
            Channel targetChannel = key.channel();

            //排除自己
            if(targetChannel instanceof SocketChannel && targetChannel != selfChannel){
                //转型
                SocketChannel socketChannel = (SocketChannel)targetChannel;
                //将msg存储到buffer
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                //将buffer的数据写入通道
                socketChannel.write(buffer);
            }
        }
    }
    public static void main(String[] args) {
        //创建一个服务器对象
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }
}
