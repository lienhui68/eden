package com.eh.eden.nio.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 多人聊天室服务端
 */
public class ChatServer {
    public static final int SERVER_PORT = 8080;

    Selector selector;
    ServerSocketChannel serverSocketChannel;
    boolean running = true;

    public void runServer() throws IOException {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();

            serverSocketChannel.bind(new InetSocketAddress(SERVER_PORT));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Server started.");

            while (running) {
                int eventCount = selector.select(100);
                if (eventCount == 0)
                    continue;
                Set<SelectionKey> set = selector.selectedKeys();
                Iterator<SelectionKey> keyIterable = set.iterator();
                while (keyIterable.hasNext()) {
                    SelectionKey key = keyIterable.next();
                    keyIterable.remove();
                    dealEvent(key);
                }
            }
        } finally {
            if (selector != null && selector.isOpen())
                selector.close();
            if (serverSocketChannel != null && serverSocketChannel.isOpen())
                serverSocketChannel.close();
        }
    }

    private void dealEvent(SelectionKey key) throws IOException {
        if (key.isAcceptable()) { // 建立连接
            System.out.println("Accept client connection.");
            SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            // 建立连接后 向客户端发送邀请注册信息
            socketChannel.write(Message.encodeRegSyn());
        }
        if (key.isReadable()) { // 处理客户端请求，包括退出聊天室
            SocketChannel socketChannel = null;
            try {
                System.out.println("Receive message from client.");
                socketChannel = (SocketChannel) key.channel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                // client主动关闭连接，需要广播给大家知道
                int ret = socketChannel.read(byteBuffer);
                if (ret == -1) {
                    throw new IOException("客户端异常关闭");
                }
                byteBuffer.flip();
                String msg = Message.CHARSET.decode(byteBuffer).toString(); // 转String
                dealMsg(msg, key); // 处理消息
            } catch (IOException e) { // client主动关闭连接，需要广播给大家知道
                socketChannel.close();
                String username = (String) key.attachment();
                if (username == null || username.equals("")) { // 未注册用户无需广播
                    return;
                }
                System.out.println(String.format("User %s disconnected", username));
                broadcast(String.format("%s disconnected", username));
            }
        }
    }

    private void dealMsg(String msg, SelectionKey key) throws IOException {
        System.out.println(String.format("Message info is: %s", msg));
        Message message = Message.decode(msg);
        if (message == null)
            return;

        SocketChannel currentChannel = (SocketChannel) key.channel();
        Set<SelectionKey> keySet = getConnectedChannel();
        switch (message.getAction()) {
            case REG_CLIENT_ACK: // 客户端注册确认
                String username = message.getMessage(); // 获取用户名
                // 去重
                for (SelectionKey keyItem : keySet) {
                    String channelUser = (String) keyItem.attachment();
                    if (channelUser != null && channelUser.equals(username)) {
                        currentChannel.write(Message.encodeRegSyn(true));
                        return;
                    }
                }
                key.attach(username);
                // 向用户发送注册确认消息
                currentChannel.write(Message.encodeRegServerAck(username));
                System.out.println(String.format("New user joined: %s,", username));
                // 对聊天室进行广播
                broadcast("welcome " + username + " join us.");
                break;
            case CHAT_MSG_SEND: // 客户端发送消息
                String toUser = message.getOption();
                String msg2 = message.getMessage();
                String fromUser = (String) key.attachment();

                for (SelectionKey keyItem : keySet) {
                    if (keyItem == key) { // 排除自己
                        continue;
                    }
                    String channelUser = (String) keyItem.attachment();
                    SocketChannel channel = (SocketChannel) keyItem.channel();
                    if (toUser == null || toUser.equals(channelUser)) {
                        channel.write(Message.encodeReceiveMsg(fromUser, msg2));
                    }
                }
                break;
        }
    }

    public void broadcast(String message) throws IOException {
        Set<SelectionKey> keySet = getConnectedChannel();
        for (SelectionKey keyItem : keySet) {
            SocketChannel channel = (SocketChannel) keyItem.channel();
            channel.write(Message.encodePublishUserList(message));
        }
    }

    private Set<SelectionKey> getConnectedChannel() {
        /*
            Selector只有执行select时才会去刷新并删除关闭的Channel
         */
        return selector.keys().stream()
                .filter(item -> item.channel() instanceof SocketChannel && item.channel().isOpen())
                .collect(Collectors.toSet());
    }

    public static void main(String[] args) throws IOException {
        new ChatServer().runServer();
    }
}