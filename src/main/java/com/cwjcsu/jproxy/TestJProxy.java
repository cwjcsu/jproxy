package com.cwjcsu.jproxy;

public class TestJProxy {
    public static void main(String[] args)
            throws InterruptedException {
        try {
            new JProxyApplication().run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
