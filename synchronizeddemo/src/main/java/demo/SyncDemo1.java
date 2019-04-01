package demo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: admin
 * @create: 2019/4/1
 * @update: 16:24
 * @version: V1.0
 * @detail:
 **/
public class SyncDemo1 {

    public static void main() {
        SyncDemo1 syncDemo1 = new SyncDemo1();
        for (int i = 0; i < 100; i++) {
            syncDemo1.addString("test" + i);
        }
    }
    private List<String> list = new ArrayList<String>();
    public synchronized void addString(String s){
        list.add(s);
    }

}
