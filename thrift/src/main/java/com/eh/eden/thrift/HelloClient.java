package com.eh.eden.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * todo
 *
 * @author David Li
 * @create 2020/09/15
 */
public class HelloClient {
    public static void main(String[] args) {

        TTransport transport = null;
        try {
            // 创建TTransport
            transport = new TSocket("localhost", 9898, 30000);

            // 创建TProtocol 协议要与服务端一致
            TProtocol protocol = new TBinaryProtocol(transport);

            // 创建client
            Hello.Client client = new Hello.Client(protocol);

            // 建立连接
            transport.open();

            // client调用server端方法
            System.out.println(client.helloString("hello"));
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } finally {
            if (null != transport) {
                transport.close(); // 请求结束，断开连接
            }
        }
    }
}
