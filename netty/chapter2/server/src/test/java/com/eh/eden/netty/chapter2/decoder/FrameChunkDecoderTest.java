package com.eh.eden.netty.chapter2.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * todo
 *
 * @author David Li
 * @create 2020/09/10
 */
public class FrameChunkDecoderTest {
    @Test
    public void testFramesDecoded() {
        // 创建一个 ByteBuf， 并存储 9 字节
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();
        // 创建一个 EmbeddedChannel， 并添加一个 FrameChunkDecoder
        EmbeddedChannel channel = new EmbeddedChannel(new FrameChunkDecoder(3));
        // 向它写入 2 字节，并断言它 们将会产生一个新帧
        assertTrue(channel.writeInbound(input.readBytes(2)));
        try {
            // 写入一个 4 字节大小 的帧，并捕获预期的 TooLongFrameException
            channel.writeInbound(input.readBytes(4));
            // 如果上面没有 抛出异常，那 么就会到达这 个断言，并且 测试失败
            fail();
        } catch (TooLongFrameException e) {
            // expected exception
        }
        // 写入剩余 的 3 字节， 并断言将 会产生一 个有效帧
        assertTrue(channel.writeInbound(input.readBytes(3)));
        // 将该 Channel 标记 为已完成状态
        assertTrue(channel.finish());

        // 读取产生的消息，并且验证值
        // Read frames
        ByteBuf read = channel.readInbound();
        assertEquals(buf.readSlice(2), read);
        read.release();

        read = channel.readInbound();
        assertEquals(buf.skipBytes(4).readSlice(3), read);
        read.release();
        buf.release();
    }
}