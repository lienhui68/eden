package com.eh.eden.netty.chapter2.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 扩展 ByteToMessageDecoder 以处理入 站字节，并将它们解码为消息
 *
 * @author David Li
 * @create 2020/09/10
 */
public class FixedLengthFrameDecoder extends ByteToMessageDecoder {

    // 要生成的帧的长度
    private final int frameLength;

    public FixedLengthFrameDecoder(int frameLength) {
        if (frameLength <= 0) {
            throw new IllegalArgumentException(
                    "frameLength must be a positive integer: " + frameLength);
        }
        this.frameLength = frameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 检查是否有足够的字 节可以被读取，以生 成下一个帧
        while (in.readableBytes() >= frameLength) {
            // 从 ByteBuf 中 读取一个新帧
            ByteBuf buf = in.readBytes(frameLength);
            // 将该帧添加到已被 解码的消息列表中
            out.add(buf);
        }
    }
}
