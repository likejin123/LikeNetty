package com.likejin.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * @Author 李柯锦
 * @Date 2023/7/14 14:18
 * @Description
 * 1.自定义一个handler，需要继承netty规定好的handlerAdpter
 * 2.这是自定义的handler才能称之为handler
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter{

    //读取数据（这里可以读取客户端发送的消息）
    /*
    1.ChannelHandlerContext ：上下文对象，含有 ： 管道pipeline，通道channel，连接地址
    2.Object msg :就是客户端发送的数据，默认是Object
    */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server ctx = " + ctx);

        //将msg转成一个ByteBuffer
        //ByteBuffer是netty提供的，不是NIO的byteBuffer
        ByteBuf buf = (ByteBuf)msg;
        System.out.println("客户端发送的消息" +  buf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址：" + ctx.channel().remoteAddress());


        //假如说这里有非常耗时的业务 - 》 异步执行 - 》 提交该channel对应的NIOEventLoop的taskQueue中
        //异步执行解决方案1：用户程序自定义普通任务执行  此时这个任务被提交到taskQueue中异步执行此时服务端不阻塞

        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(10 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端",CharsetUtil.UTF_8));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        //异步解决方案2：用户自定义定时任务 -》 该任务提交到scheduleTaskQueue
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(10 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端",CharsetUtil.UTF_8));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        },5, TimeUnit.SECONDS);


        System.out.println("go on");
    }



    //数据读取完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        //write + flush
        //将数据写入到缓冲并且刷新（把缓冲数据刷新到管道）
        //一般讲：对这个发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端",CharsetUtil.UTF_8));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
    }



}
