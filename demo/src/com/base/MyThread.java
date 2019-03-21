package com.base;

/**
 * @author: admin
 * @create: 2019/3/21
 * @update: 16:46
 * @version: V1.0
 * @detail:
 **/
public class MyThread extends Thread{
    public void run(){
        System.out.println("MyThread.run()");
    }


    public static void main(String[] args) {

        MyThread myThread1 = new MyThread();
        MyThread myThread2 = new MyThread();

        myThread1.run();
        myThread2.run();
    }
}
