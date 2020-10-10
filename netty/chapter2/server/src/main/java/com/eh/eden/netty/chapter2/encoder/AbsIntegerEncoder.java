package com.eh.eden.netty.chapter2.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * 扩展 MessageToMessageEncoder 以 将一个消息编码为另外一种格式
 *
 * @author David Li
 * @create 2020/09/10
 */
public class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        // 检查是否有足够 的字节用来编码
        while (msg.readableBytes() >= 4) {
            // 从输入的 ByteBuf 中读取下一个整数， 并且计算其绝对值
            int value = Math.abs(msg.readInt());
            // 将该整数写入到编码 消息的 List 中
            out.add(value);
        }
    }
}
