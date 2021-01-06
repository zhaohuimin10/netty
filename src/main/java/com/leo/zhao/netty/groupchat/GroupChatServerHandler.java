package com.leo.zhao.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    //定义一个channel组，管理所有的channel
    //GlobalEventExecutor.INSTANCE 是全局事件执行器，是一个单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //handlerAdded 表示建立连接，一旦建立连接，第一个被执行
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //将channelGroup里所有的channel遍历，并发送消息
        channelGroup.writeAndFlush("[客户端] " + channel.remoteAddress() + " 加入聊天" + simpleDateFormat.format(new Date()) + "\n");
        channelGroup.add(channel);
    }

    //表示channel 处于活动状态 提示 xx上线
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " 上线了~");
    }


    //此方法会将channelGroup里对应的channel自动删掉
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //将channelGroup里所有的channel遍历，并发送消息
        channelGroup.writeAndFlush("[客户端] " + channel.remoteAddress() + " 离线了\n");
    }


    /*
     * 读取数据  ❤
     * */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();

        channelGroup.forEach(ch -> {
            if (channel != ch) {
                ch.writeAndFlush("[客户] " + channel.remoteAddress() + " 说" + msg + "(" + simpleDateFormat.format(new Date()) + ")\n");
            }
        });
    }

    //关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
