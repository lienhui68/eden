package com.eh.eden.netty.chapter2.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


/**
 * todo
 *
 * @author David Li
 * @create 2020/09/10
 */
public class FixedLengthFrameDecoderTest {

    @Test
    public void testFramesDecoded() {
        // 创建一个 ByteBuf， 并存储 9 字节
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();
        // 创建一个 EmbeddedChannel， 并添 加一个 FixedLengthFrameDecoder， 其将以 3 字节的帧长度被测试
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        // write bytes
        // 将数据写入 EmbeddedChannel
        assertTrue(channel.writeInbound(input.retain()));
        // 标记 Channel 为已完成状态
        assertTrue(channel.finish());

        // read messages
        // 读取所生成的消息，并 且验证是否有 3 帧（切 片），其中每帧（切片） 都为 3 字节
        ByteBuf read = channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        assertNull(channel.readInbound());
        buf.release();
    }
    
    /**
     * 第二个测试方法
     */
    @Test
    public void testFramesDecoded2() {
        // 创建一个 ByteBuf， 并存储 9 字节
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();
        // 创建一个 EmbeddedChannel， 并添 加一个 FixedLengthFrameDecoder， 其将以 3 字节的帧长度被测试
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));

        // 返回 false， 因为 没有一个完整的 可供读取的帧
        assertFalse(channel.writeInbound(input.readBytes(2)));
        assertTrue(channel.writeInbound(input.readBytes(7)));

        // 标记 Channel 为已完成状态
        assertTrue(channel.finish());

        // read messages
        // 读取所生成的消息，并 且验证是否有 3 帧（切 片），其中每帧（切片） 都为 3 字节
        ByteBuf read = channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        assertNull(channel.readInbound());
        buf.release();
    }
}