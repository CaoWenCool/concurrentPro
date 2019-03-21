package com.optimization;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author: admin
 * @create: 2019/3/21
 * @update: 17:07
 * @version: V1.0
 * @detail:
 **/
public class PrintProcessor extends Thread implements RequestProcessor{

    LinkedBlockingDeque<Request> requests = new LinkedBlockingDeque<Request>();

    private final RequestProcessor nextProcessor;

    public PrintProcessor(RequestProcessor nextProcessor){
        this.nextProcessor = nextProcessor;
    }

    @Override
    public void run() {
        while (true){
            try{
                Request request = requests.take();
                System.out.println("print data:"+request.getName());
                nextProcessor.processRequest(request);
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
