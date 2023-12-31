package com.likejin.netty.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @Author 李柯锦
 * @Date 2023/7/14 21:25
 * @Description 空闲事件处理器
 */
public class MySeverHandler extends ChannelInboundHandlerAdapter {

    /*
     * @Description
     * @param ctx 上下文
     * @param evt 事件
     * @return void
     **/
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if(evt instanceof IdleStateEvent){
            //将evt向下转型 IdleStateEvent
            IdleStateEvent event = (IdleStateEvent) evt;
            String eventType = null;
            //判断事件
            switch (event.state()){
                case READER_IDLE:
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
            }

            System.out.println(ctx.channel().remoteAddress() + "--超时事件发生--" + eventType);

            //此时对应通道发生了读写空闲
            System.out.println("服务器响应处理");

            //如果发生空闲，我们关闭通道
            ctx.channel().close();
        }
    }
}
