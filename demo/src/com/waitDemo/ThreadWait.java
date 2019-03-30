package com.waitDemo;

/**
 * Created by 曹文 on 2019/3/29.
 */
public class ThreadWait extends  Thread{
    private Object lock;
    public ThreadWait(Object lock){
        this.lock = lock;
    }

    @Override
    public void run() {
        synchronized (lock){
            System.out.println("开始执行 Thread  wait");
            try{
                lock.wait();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            System.out.println("执行结束  thread wait");
        }
    }
}
