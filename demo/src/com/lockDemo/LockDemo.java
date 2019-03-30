package com.lockDemo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by 曹文 on 2019/3/29.
 */
public class LockDemo {
    static Map<String,Object> cacheMap = new HashMap<>();
    static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    static Lock read = rwl.readLock();
    static Lock write = rwl.writeLock();

    public static final Object get(String key){
        System.out.println("开始读取数据");
        read.lock();//读锁
        try{
            return cacheMap.get(key);
        }finally {
            read.unlock();
        }
    }

    public static final Object put(String key,Object value){
        write.lock();
        System.out.println("开始写数据");
        try{
            return cacheMap.put(key,value);
        }finally {
            write.unlock();
        }
    }

    static final class Node{
        int waitStatus; //表示节点的状态，包含cancelled(取消)
        //condition 表示节点等待condition也就是在condition队列中
        Node prev;//前继节点
        Node next;//后继节点
        Node nextWaiter;//存储在condition队列中的后继节点
        Thread thread;//当前线程
    }
}
