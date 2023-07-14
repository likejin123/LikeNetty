package com.likejin.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @Author 李柯锦
 * @Date 2023/7/14 21:48
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
                            ChannelPipeline pipeline = ch.pipeline();

                            //因为基于http协议，使用http的编码解码器
                            pipeline.addLast(new HttpServerCodec());
                            //是以块方式写的，添加chunkedwriter处理器
                            pipeline.addLast(new ChunkedWriteHandler());
                            /*
                              1.http数据在传输过程是分段的。HttpObjectAggregator 可以将多个段聚合。
                              2.这就是为什么当浏览器发送大量数据时就回发出多次http请求
                             **/
                            pipeline.addLast(new HttpObjectAggregator(8192));
                            /*
                            1.对于websocket ，他的数据是以帧的形式传递
                            2.可以看到 WebSocketFrame的六个子类
                            3.浏览器请求时：ws://localhost:7000/xxx 表示请求的uri
                            4.WebSocketServerProtocolHandler核心功能将http协议升级为ws协议  保持长链接
                             **/
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));

                            //自定义的handler，处理业务逻辑
                            pipeline.addLast(new MyTextWebSocketFrameHandler());
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
