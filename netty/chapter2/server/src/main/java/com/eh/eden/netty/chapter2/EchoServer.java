package com.eh.eden.netty.chapter2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * todo
 *
 * @author David Li
 * @create 2020/09/03
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    private void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        // 1. 创建EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 2. 创建ServerBootstrap
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    // 3. 指定所使用的 NIO 传输 Channel
                    .channel(NioServerSocketChannel.class)
                    // 4. 使用指定的 端口设置套 接字地址
                    .localAddress(new InetSocketAddress(port))
                    // 5. 添加一个 EchoServerHandler 到子 Channel 的 ChannelPipeline
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // EchoServerHandler 被标注为@Shareable，所 以我们可以总是使用 同样的实例
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            // 异步地绑定服务器； 调用 sync()方法阻塞 等待直到绑定完成
            ChannelFuture f = b.bind().sync();
            // 获取 Channel 的 CloseFuture， 并且阻塞当前线程直到它完成
            f.channel().closeFuture().sync();
        } finally {
            // 关闭 EventLoopGroup， 释放所有的资源
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        // 参数校验
        if (args.length != 1) {
            System.err.println("Usage: " + EchoServer.class.getSimpleName() + "<port>");
            return;
        }

        // 设置端口值（如果端口参数 的格式不正确，则抛出一个 NumberFormatException）
        int port = Integer.parseInt(args[0]);
        // 调用服务器 的 start()方法
        new EchoServer(port).start();
    }
}
