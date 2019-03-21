package com.optimization;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author: admin
 * @create: 2019/3/21
 * @update: 17:12
 * @version: V1.0
 * @detail:
 **/
public class SaveProcessor extends Thread implements RequestProcessor{
    LinkedBlockingQueue<Request> requests =  new LinkedBlockingQueue<Request>();

    public void run(){
        while(true){
            try{
                Request request = requests.take();
                System.out.println("begin save request info:"+request);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    //处理请求
    @Override
    public void processRequest(Request request) {
        requests.add(request);
    }
}
