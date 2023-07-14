package com.likejin.bio;



import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author 李柯锦
 * @Date 2023/7/13 15:41
 * @Description
 */


public class BIOServer {

    public static void main(String[] args) throws  Exception {
        //思路
        //1.创建一个线程池
        //2.如果有客户端连接，就创建一个线程，与之通讯（单独方法）

        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();

        //创建一个ServerSocket
        ServerSocket serverSocket = new ServerSocket(6666);

        System.out.println("服务器启动了");


        while(true){

            //监听，等待客户端连接

            //阻塞等待客户端连接
            System.out.println("accept...................");
            final Socket socket = serverSocket.accept();

            System.out.println("连接一个客户端");

            //创建一个线程，与之通讯（单独写一个方法）
            newCachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    //重写run方法。可以和客户端通讯。
                    handler(socket);
                }
            });
        }

    }


    //编写一个handler方法和客户端通讯
    public static void handler(Socket socket){
        try{
            System.out.println("线程id = " + Thread.currentThread().getName());
            byte[] bytes = new byte[1024];
            //通过socket获取输入流
            InputStream inputStream = socket.getInputStream();

            //循环读取客户端发送的数据
            while(true){

                //等待客户端数据
                System.out.println("read .................");
                int read = inputStream.read(bytes);
                if(read != -1){
                    System.out.println(new String(bytes,0,read));//输出客户端发送的数据
                }else{
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println("关闭和client连接");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
