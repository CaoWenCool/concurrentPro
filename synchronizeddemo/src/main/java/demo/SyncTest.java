package demo;

/**
 * @author: admin
 * @create: 2019/4/1
 * @update: 9:43
 * @version: V1.0
 * @detail:
 **/
public class SyncTest {

    public void syncBlock(){
        synchronized(this){
            System.out.println("hello block");
        }
    }

    public synchronized void syncMethod(){

        System.out.println("hello method");
    }
}
