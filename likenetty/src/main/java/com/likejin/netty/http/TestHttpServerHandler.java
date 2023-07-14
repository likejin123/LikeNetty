package com.likejin.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * @Author 李柯锦
 * @Date 2023/7/14 15:50
 * @Description
 * SimpleChannelInboundHandler 是 子类
 * HttpObject 表示客户端和服务器端相互通讯的数据被封装为httpObject
 */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {


    //当有读取事件的时候 触发该函数 读取客户端数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        //判断msg是不是一个httpRequst请求
        if(msg instanceof HttpRequest){

            //对特定资源进行过滤
            //获取到httprequest对象
            HttpRequest httpRequest = (HttpRequest)msg;
            //获取uri
            URI uri = new URI(httpRequest.uri());
            if("/favicon.ico".equals(uri.getPath())){
                System.out.println("请求了favicon.ico 不做响应 ");
                return;
            }

            System.out.println("msg 类型" + msg.getClass() + "客户端地址" + ctx.channel().remoteAddress());
            //回复信息给浏览器
            ByteBuf content = Unpooled.copiedBuffer("hello，我是服务器", CharsetUtil.UTF_8);

            //构造一个http的响应 httpResponse
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,content);

            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain;charset=utf-8");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());

            //将构建好的response返回
            ctx.writeAndFlush(response);
        }
    }
}
