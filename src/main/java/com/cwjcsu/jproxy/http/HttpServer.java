package com.cwjcsu.jproxy.http;


import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.ProxyAuthenticator;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

public class HttpServer {

    public static void main(String[] args) {
        HttpProxyServer server =
                DefaultHttpProxyServer.bootstrap()
                        .withPort(8080)
                        .withProxyAuthenticator(new ProxyAuthenticator() {
                            public boolean authenticate(String userName, String password) {
                                return "root".equalsIgnoreCase(userName) && "123456".equalsIgnoreCase(password);
                            }

                            public String getRealm() {
                                return null;
                            }
                        })//.withManInTheMiddle(new SelfSignedMitmManager())
                        .start();
    }
}
