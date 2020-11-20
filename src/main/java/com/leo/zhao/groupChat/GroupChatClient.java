package com.leo.zhao.groupChat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * 穷聊客户端
 */
public class GroupChatClient {
    private final String host = "127.0.0.1";
    private final int port = 6667;
    private Selector selector;
    private SocketChannel socketChannel;
    private String userName;

    public GroupChatClient() throws IOException {
        selector = Selector.open();
        socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        userName = socketChannel.getRemoteAddress().toString();
        System.out.println(userName + "is ok");
    }

    public static void main(String[] args) throws IOException {
        GroupChatClient groupChatClient = new GroupChatClient();
        new Thread(() -> {
            try {
                groupChatClient.listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String s = scanner.nextLine();
            groupChatClient.sendInfo(s);
        }
    }

    private void sendInfo(String s) throws IOException {
//        s = userName + " 说" + s;
        socketChannel.write(ByteBuffer.wrap(s.getBytes()));
    }

    private void listen() throws IOException {
        while (true) {
            try {
                int select = selector.select();
                if (select > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        if (selectionKey.isReadable()) {
                            SocketChannel channel = (SocketChannel) selectionKey.channel();
                            channel.configureBlocking(false);
                            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                            int read = channel.read(byteBuffer);
                            if (read > 0) {
                                System.out.println(new String(byteBuffer.array()));
                            }
                        }
                        iterator.remove();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
