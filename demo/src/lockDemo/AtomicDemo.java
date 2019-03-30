package lockDemo;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by 曹文 on 2019/3/29.
 */
public class AtomicDemo {

    private static int count = 0;
    static Lock lock = new ReentrantLock();
    public static void inc(){
        lock.lock();
        try{
            Thread.sleep(1);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args)throws InterruptedException {
        for(int i=0;i<1000;i++){
            new Thread(() ->{
                AtomicDemo.inc();
            }).start();
        }
        Thread.sleep(30000);
        System.out.println("result:"+count);
    }
}
