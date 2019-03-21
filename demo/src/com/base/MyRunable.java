package com.base;

/**
 * @author: admin
 * @create: 2019/3/21
 * @update: 16:49
 * @version: V1.0
 * @detail:
 **/
public class MyRunable implements Runnable{
    @Override
    public void run() {
        System.out.println("MyRunable.run()");
    }

    public static void main(String[] args) {
        MyRunable myRunable1 = new MyRunable();
        MyRunable myRunable2 = new MyRunable();
        myRunable1.run();
        myRunable2.run();
    }
}
