package com.leo.zhao;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


public class NioServer {


    public static void main(String[] args) throws IOException {
        //打开Selector为了它可以轮询每个 Channel 的状态
        Selector selector = Selector.open();
        //创建一个连接通道ServerSocketChannel
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);//设置为非阻塞方式
        ssc.socket().bind(new InetSocketAddress(8089));
        ssc.register(selector, SelectionKey.OP_ACCEPT);//注册监听的事件
        while (true) {

            if (selector.select(1000) == 0) {
                System.out.println("服务器等待了一秒，暂无连接！！");
                continue;
            }
//            selector.select();

            Set selectedKeys = selector.selectedKeys();//取得所有key集合
            Iterator it = selectedKeys.iterator();
            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();
                //注册
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();//因为有链接，所以绝对不会阻塞
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    System.out.println("连接到的客户端为：" + sc.hashCode());
                }
                //读取数据
                else if (key.isReadable()) {
                    SocketChannel sc = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                    sc.read(byteBuffer);
                    System.out.println("读取到的数据为：" + new String(byteBuffer.array()));
                }
                //手动删除当前的selecttionkey，防止重复操作！
                it.remove();
            }
        }
    }
}
