package com.cwjcsu.jproxy;

import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;

import java.util.Arrays;

public class JProxy implements WrapperListener {
    private static JProxyApplication application;

    public static void main(String[] args)
            throws Exception {
        try {
            application = new JProxyApplication();
            application.run(args);
            WrapperManager.start(new JProxy(), args);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 执行stop操作，停止服务
     */
    public static void stop() {
        application.close();
        try {
            WrapperManager.stop(0);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        System.exit(0);
    }

    public static void forceStop(int exitCode) {
        application.close();
        try {
            WrapperManager.stop(exitCode);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        System.exit(exitCode);
    }

    /**
     * 执行restart操作
     */
    public static void restart() {
        application.close();
        try {
            WrapperManager.restart();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * jws回调
     *
     * @param event
     */
    public void controlEvent(int event) {
        System.out.println("on JWS controlEvent(" + event + ")");
        if ((event != WrapperManager.WRAPPER_CTRL_LOGOFF_EVENT) || (!WrapperManager.isLaunchedAsService())) {
            System.out.println("on JWS WrapperManager.stop(0)");
            WrapperManager.stop(0);
        }
    }

    /**
     * jws回调
     *
     * @param args
     * @return
     */
    public Integer start(String[] args) {
        System.out.println("started by JWS with args:" + Arrays.asList(args));
        return null;
    }

    public int stop(int exitCode) {
        System.out.println("stop by JWS with exitCode " + exitCode + "");
        return exitCode;
    }

}
