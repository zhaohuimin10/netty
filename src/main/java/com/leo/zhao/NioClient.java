package com.leo.zhao;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NioClient {

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8089);
        socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
        boolean connect = socketChannel.connect(inetSocketAddress);
        if (!connect) {
            while (!socketChannel.finishConnect()) {
                System.out.println("因连接需要时间，客户端不会阻塞，可以做其他工作。。。");
            }
        }
        while (true) {
            Scanner input = new Scanner(System.in);
            String s = input.nextLine();
            ByteBuffer byteBuffer = ByteBuffer.wrap(s.getBytes());
            socketChannel.write(byteBuffer);
            if (s.equals("exit")) {
                break;
            }
        }

    }
}
