package com.eh.eden.thrift;

import org.apache.thrift.TException;

/**
 * todo
 *
 * @author David Li
 * @create 2020/09/15
 */
public class HelloServiceImpl implements Hello.Iface {
    @Override
    public String helloString(String para) throws TException {
        return para + ", world.";
    }
}
