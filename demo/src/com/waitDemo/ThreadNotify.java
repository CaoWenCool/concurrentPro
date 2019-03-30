package com.waitDemo;

/**
 * Created by 曹文 on 2019/3/29.
 */
public class ThreadNotify extends Thread{
    private  Object lock;

    public ThreadNotify(Object lock){
        this.lock = lock;
    }

    @Override
    public void run() {
        synchronized (lock){
            System.out.println("开始执行 thread notify");
            lock.notify();
            System.out.println("执行结束 thread notify");
        }
    }
}
