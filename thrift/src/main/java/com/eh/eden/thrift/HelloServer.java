package com.eh.eden.thrift;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

/**
 * todo
 *
 * @author David Li
 * @create 2020/09/15
 */
public class HelloServer {
    public static void main(String[] args) {
        try {
            // transport
            TServerSocket serverTransport = new TServerSocket(9898);

            // processor
            TProcessor tprocessor = new Hello.Processor<Hello.Iface>(new HelloServiceImpl());
            TServer.Args tArgs = new TServer.Args(serverTransport);
            tArgs.processor(tprocessor)
                    .protocolFactory(new TBinaryProtocol.Factory());

            // Server
            TServer server = new TSimpleServer(tArgs);
            System.out.println("服务端开启....");
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}
