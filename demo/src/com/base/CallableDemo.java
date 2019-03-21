package com.base;

import java.util.concurrent.*;

/**
 * @author: admin
 * @create: 2019/3/21
 * @update: 16:54
 * @version: V1.0
 * @detail:
 **/
public class CallableDemo implements Callable<String>{
    public static void main(String[] args)throws ExecutionException,InterruptedException {
        ExecutorService executionServcie = Executors.newFixedThreadPool(1);
        CallableDemo callableDemo = new CallableDemo();
        Future<String> future = executionServcie.submit(callableDemo);
        System.out.println(future.get());
        executionServcie.shutdown();
    }

    @Override
    public String call() throws Exception {
        int a = 1;
        int b = 2;
        System.out.println(a+b);

        return "执行结果"+(a+b);
    }
}
