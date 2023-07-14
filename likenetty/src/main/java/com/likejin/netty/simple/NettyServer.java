package com.likejin.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Author 李柯锦
 * @Date 2023/7/14 14:04
 * @Description
 */
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {

        //创建BossGroup  和 WorkerGroup
        //说明
        //1.创建两个线程组 bossGroup 和 workerGroup
        //2.bossGroup处理连接请求，真正和客户端业务处理，会交给workerGroup完成
        //3.两个都是无线循环
        // 4.bossgroup 和 workergroup 含有的（NIOEventLoop）的个数 是默认的CPU合数 * 2
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup =new NioEventLoopGroup();

        try{
            //创建服务器端的启动对象，配置参数
            ServerBootstrap bootstarp = new ServerBootstrap();

            //使用链式变成来进行设置参数
            bootstarp.group(bossGroup,workerGroup) //设置两个线程组
                    .channel(NioServerSocketChannel.class) //使用NIOServerSocketChannel作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG,128) //设置线程队列得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE,true) //设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() { //创建一个通道初始化对象（匿名对象）
                        //向pipeline设置处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    }); //workergroup 的EventLoop 对应的管道设置处理器

            System.out.println("...服务器 is ready...");


            //绑定一个端口并且通不处理，生成了一个ChannelFuture对象
            //启动服务器并绑定端口
            ChannelFuture cf = bootstarp.bind(6668).sync();


            //给cf注册监听器，监控关心的事件
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(cf.isSuccess()){
                        System.out.println("监听端口成功");
                    }else{
                        System.out.println("监听端口是啊比");
                    }
                }
            });
            //对关闭通道进行监听
            cf.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }
}
