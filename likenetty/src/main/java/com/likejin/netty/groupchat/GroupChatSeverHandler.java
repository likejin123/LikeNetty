package com.likejin.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;

/**
 * @Author 李柯锦
 * @Date 2023/7/14 19:16
 * @Description
 */
public class GroupChatSeverHandler extends SimpleChannelInboundHandler<String> {

    //定义一个channel组 管理所有的channel
    //lobalEventExecutor.INSTANCE是一个全局的事件执行器，是一个单例。
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //当连接建立时。一旦连接，第一个被执行
    //将当前channel加入到channelGroup
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();
        //将该客户加入聊天的信息推送给其他在线的客户
        //该方法会将channelgroup中所有的channel遍历并且发送消息，不需要自己遍历
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "加入聊天\n" );

        channelGroup.add(channel);
    }

    //表示断开连接会被触发,将xx客户离开信息推送给当前在线的客户（自动去除当前channel）
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "退出聊天\n");
        System.out.println("channelGroup Size" + channelGroup.size());
    }

    //表示channel处于活动状态，提示xx上线
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "上线了");
    }

    //表示channel处于不活动状态，提示 xx离线了
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "离线了");
    }

    //读取数据并且转发给所有在线的用户
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //获取当前channel
        Channel channel = ctx.channel();
        //这是遍历channelGroup，根据不同的情况，回送不同的消息
        channelGroup.forEach( ch ->{
            if(channel != ch){ //不是当前channel，转发消息
                ch.writeAndFlush("[客户]" + channel.remoteAddress() + "发送了消息" + msg);
            }else{//回显自己发送的消息
                ch.writeAndFlush("[自己]发送了消息" + msg + "\n");
            }
        });
    }
    //如果发生了异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        //关闭
        ctx.close();
    }

}
