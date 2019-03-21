package com.interrupt;

import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author: admin
 * @create: 2019/3/21
 * @update: 18:11
 * @version: V1.0
 * @detail:
 **/
public class InterruptDemo {
    private static int i;

//    public static void main(String[] args)throws InterruptedException{
//        Thread thread = new Thread(()->{
//            while(!Thread.currentThread().isInterrupted()){
//                i++;
//            }
//            System.out.println("Num"+i);
//        },"interruptDemo");
//        thread.start();
//        TimeUnit.SECONDS.sleep(1);
//        thread.interrupt();
//    }

    public static void main(String[] args)throws InterruptedException {
        Thread thread = new Thread(()->{
            while (true){
                boolean ii = Thread.currentThread().isInterrupted();
                if(ii){
                    System.out.println("before"+ii);
                    Thread.interrupted();//对线程进行复位，中断标识位false
                    System.out.println("after："+Thread.currentThread().isInterrupted());
                }
            }
        });
        thread.start();
        TimeUnit.SECONDS.sleep(1);
        thread.interrupt();//设置中断标识，中断标识位true
    }
}
