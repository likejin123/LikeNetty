package com.likejin.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @Author 李柯锦
 * @Date 2023/7/14 21:15
 * @Description
 */
public class MyServer {

    public static void main(String[] args) throws InterruptedException {
        //创建两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO)) //在bossgroup增加日志处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //获取到pipline
                            ChannelPipeline pipeline = ch.pipeline();
                            //加入一个netty提供的IdleStateHandler
                            /*
                            1.netty提供的处理空闲状态的处理器。
                            2.三个参数
                                readerIdleTime：表示多长时间没有读了，此时发送一个心跳检测包，检测是否还是连接状态
                                WriterIdleTime：表示多长时间没有写了，此时发送一个心跳检测包，检测是否还是连接状态
                                alldleTime：多长时间没有读写了，发送一个心跳检测包，检测是否是连接状态
                                此时会触发事件（读空闲事件，写空闲事件，读写空闲事件）
                            3.明明客户端退出连接在handler中可以检测到，为什么还要心跳检测呢？
                                有时服务器断了，但是sever无法感知，需要心跳包来准确感知是否连接有效。
                            4.当事件触发后，就回传递给管道的下个handler来处理
                            通过调用（触发）下一个handler的userEventTiggered方法来处理 上述事件

                            5.发送心跳包，如果能触发事件，此时连接正常。如果触发不了事件，则连接断开
                             **/
                            pipeline.addLast(new IdleStateHandler(3,5,7, TimeUnit.SECONDS));
                            /*
                            加入一个对空闲检测进一步处理的handler（自定义）
                            */
                            pipeline.addLast(new MySeverHandler());
                        }
                    });
            System.out.println("netty 服务器启动");

            ChannelFuture channelFuture = bootstrap.bind(7000).sync();
            //监听channel的关闭时间
            channelFuture.channel().closeFuture().sync();

        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
