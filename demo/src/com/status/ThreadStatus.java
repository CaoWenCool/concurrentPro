package com.status;

import java.util.concurrent.TimeUnit;

/**
 * @author: admin
 * @create: 2019/3/21
 * @update: 17:41
 * @version: V1.0
 * @detail:
 **/
public class ThreadStatus {

    public static void main(String[] args) {
        new Thread(()->{
            while (true){
                try {
                    TimeUnit.SECONDS.sleep(100);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        },"timewaiting").start();
        //WAITING,线程在ThreadStatus类锁上通过wait进行等待
        new Thread(()->{
            while (true){
                synchronized (ThreadStatus.class){
                    try {
                        ThreadStatus.class.wait();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        },"Waiting").start();
        //线程在ThreadStatus加锁侯，不会释放锁
        new Thread(new BlockedDemo(),"BlockDemo-01").start();
        new Thread(new BlockedDemo(),"BlockDemo-02").start();
    }

    static class BlockedDemo extends Thread{
        public void run(){
            synchronized (BlockedDemo.class){
                while (true){
                    try {
                        TimeUnit.SECONDS.sleep(100);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
