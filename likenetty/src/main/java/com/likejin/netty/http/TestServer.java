package com.likejin.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Author 李柯锦
 * @Date 2023/7/14 15:50
 * @Description
 */
public class TestServer {

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup =new NioEventLoopGroup();

        try{

            ServerBootstrap bootstarp = new ServerBootstrap();

            bootstarp.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new TestServerInitializer());//为每个连接分配通道并且分配处理器

            ChannelFuture channelFuture = bootstarp.bind(8080).sync();
            System.out.println("服务器监听8080端口");
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
