package com.eh.eden.nio.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 多人聊天室客户端
 */
public class ChatClient {
    Selector selector;
    SocketChannel socketChannel;
    boolean running = true;

    MessageType messageType = MessageType.REG_CLIENT_ACK;
    private final static String PROMPT_USERNAME = "Username:";
    private final static String PROMPT_INPUT = "Input the message:";

    public void runClient() throws IOException {
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress("127.0.0.1", ChatServer.SERVER_PORT));
            System.out.println("Client connecting to server.");

            socketChannel.register(selector, SelectionKey.OP_CONNECT);

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

            if (socketChannel != null && socketChannel.isConnected())
                socketChannel.close();
        }
    }

    private void dealEvent(SelectionKey key) throws IOException {
        // 建立连接后，主线程负责处理来自服务端的消息
        if (key.isConnectable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            if (channel.isConnectionPending()) {
                channel.finishConnect();
            }
            channel.register(selector, SelectionKey.OP_READ);

            // 建立连接后，起一个新线程专门负责向服务端发送消息
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    while (running) {
                        String msg = reader.readLine();
                        if (msg == null || msg.length() == 0)
                            continue;

                        if (messageType == MessageType.REG_CLIENT_ACK) {
                            ByteBuffer bufferMsg = Message.encodeRegClientAck(msg);
                            channel.write(bufferMsg);
                        } else {
                            String[] msgArr = msg.split("#", 2);
                            ByteBuffer bufferMsg = Message.encodeSendMsg(msg);
                            if (msgArr.length == 2) {
                                bufferMsg = Message.encodeSendMsg(msgArr[0], msgArr[1]);
                            }

                            channel.write(bufferMsg);
                        }
                        printPrompt(PROMPT_INPUT);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                }
            }).start();
        } else if (key.isReadable()) {
            try {
                SocketChannel channel = (SocketChannel) key.channel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                channel.read(byteBuffer);
                byteBuffer.flip();
                String msg = Message.CHARSET.decode(byteBuffer).toString();
                dealMsg(msg);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Server exit.");
                System.exit(0);
            }
        }
    }

    private void dealMsg(String msg) {
        Message message = Message.decode(msg);
        if (message == null)
            return;

        switch (message.getAction()) {
            case REG_SERVER_SYN: // 服务端注册邀请消息
                printMsgAndPrompt(message.getMessage(), PROMPT_USERNAME);
                break;
            case REG_SERVER_ACK:
                messageType = MessageType.CHAT_MSG_SEND; // 服务端注册确认消息
                printMsgAndPrompt(message.getMessage(), PROMPT_INPUT);
                break;
            case CHAT_MSG_RECEIVE: // 接受聊天消息
                String info = "from " + message.getOption() + ": " + message.getMessage();
                printMsgAndPrompt(info, PROMPT_INPUT);
                break;
            default:
        }
    }

    private void printPrompt(String msg) {
        System.out.print(msg);
    }

    private void printMsgAndPrompt(String msg, String prompt) {
        System.out.println();
        System.out.println(msg);
        System.out.print(prompt);
    }

    public static void main(String[] args) throws IOException {
        new ChatClient().runClient();
    }
}
