package com.eh.eden.netty.chapter2.encoder;

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
public class AbsIntegerEncoderTest {
    @Test
    public void testEncoded() {
        // 创建一个 ByteBuf， 并且 写入 9 个负整数
        ByteBuf buf = Unpooled.buffer();
        for (int i = 1; i < 10; i++) {
            buf.writeInt(i * -1);
        }
        // 创建一个EmbeddedChannel， 并安装一个要测试的 AbsIntegerEncoder
        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());
        // 写入 ByteBuf， 并断言调 用 readOutbound()方法将 会产生数据
        assertTrue(channel.writeOutbound(buf));
        // 将该 Channel 标记为已 完成状态
        assertTrue(channel.finish());
        // read bytes
        // 读取所产生的消息， 并断言它们包含了对 应的绝对值
        for (int i = 1; i < 10; i++) {
            assertEquals((Integer) i, channel.readOutbound());
        }

        assertNull(channel.readOutbound());
    }
}