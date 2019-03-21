package com.optimization;

/**
 * @author: admin
 * @create: 2019/3/21
 * @update: 17:15
 * @version: V1.0
 * @detail:
 **/
public class DemoTest {

    PrintProcessor printProcessor;

    protected DemoTest(){
        SaveProcessor saveProcessor = new SaveProcessor();
        saveProcessor.start();
        printProcessor = new PrintProcessor(saveProcessor);
        printProcessor.start();
    }
    private void doTest(Request request){
        printProcessor.processRequest(request);
    }

    public static void main(String[] args) {
        Request request = new Request();

        request.setName("cc");
        new DemoTest().doTest(request);
    }
}
