package com.volatileDemo;

/**
 * @author: admin
 * @create: 2019/3/22
 * @update: 8:34
 * @version: V1.0
 * @detail:
 **/
public class VolatileDemo {
    private volatile static boolean stop = false;
//    public static void main(String args[])throws InterruptedException{
//        Thread thread = new Thread(()->{
//           int i =0;
//           while (!stop){
//               i++;
//           }
//        });
//        thread.start();
//        System.out.println("begin start thread");
//        Thread.sleep(1000);
//        stop = true;
//
//    }

    volatile int i;
    public  void  incr(){
        i++;
        System.out.println(i);
    }
    public static void main(String[] args) {
        new VolatileDemo().incr();
    }
}
