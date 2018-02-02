package com.cwjcsu.jproxy;

import com.cwjcsu.jproxy.socks.SocksServer;
import com.cwjcsu.jproxy.socks.SocksServerConfig;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.ProxyAuthenticator;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.util.Properties;

public class JProxyApplication {
    private Logger logger = LoggerFactory.getLogger(JProxyApplication.class);
    private HttpProxyServer proxyServer;
    private SocksServer socksServer;

    public void run(String[] args) throws Exception {

        Properties properties = loadConf();
        Runtime.getRuntime().addShutdownHook(jvmShutdownHook);
        boolean httpEnable = Boolean.parseBoolean(properties.getProperty("http.enable"));
        if (httpEnable) {
            final String user = properties.getProperty("http.user");
            final String password = properties.getProperty("http.password");
            int port = Integer.parseInt(properties.getProperty("http.port", "9521"));
            final String host = properties.getProperty("http.host", "127.0.0.1");
            HttpProxyServerBootstrap bootstrap = DefaultHttpProxyServer.bootstrap();
            bootstrap.withAddress(new InetSocketAddress(host, port));
            if (user != null && user.length() > 0) {
                if (password == null || password.length() < 5) {
                    throw new IOException("http proxy server password length must greater than 6");
                }
                bootstrap.withProxyAuthenticator(new ProxyAuthenticator() {
                    public boolean authenticate(String userName, String pwd) {
                        return user.equals(userName) && password.equals(pwd);
                    }

                    public String getRealm() {
                        return null;
                    }
                });
            }
            proxyServer = bootstrap.start();
        }
        boolean socksEnable = Boolean.parseBoolean(properties.getProperty("socks.enable"));
        if (socksEnable) {
            String user = properties.getProperty("socks.user");
            String password = properties.getProperty("socks.password");
            int port = Integer.parseInt(properties.getProperty("socks.port", "9522"));
            final String host = properties.getProperty("socks.host", "127.0.0.1");

            SocksServerConfig socksConfig = new SocksServerConfig();
            socksConfig.setBindPort(port);
            socksConfig.setBindAddress(host);
            if (user != null && user.length() > 0) {
                if (password == null || password.length() < 5) {
                    throw new IOException("socks server password length must greater than 6");
                }
                socksConfig.setAuthentication(new PasswordAuthentication(user, password.toCharArray()));
            }
            socksServer = new SocksServer(socksConfig);
            try {
                socksServer.start();
            } catch (Exception e) {
                logger.error("start socks server fail", e);
            }
        }
    }

    private final Thread jvmShutdownHook = new Thread(new Runnable() {
        @Override
        public void run() {
            close();
        }
    }, "JProxy-shutdown-hook");

    private Properties loadConf() throws IOException {
        String configFile = System.getProperty("jproxy.conf");
        if (configFile == null) {
            throw new IOException("-Djproxy.conf file not set");
        }
        File file = new File(configFile);
        if (!file.exists()) {
            logger.error("file {} not exists", file);
            throw new IOException("jproxy.conf not found " + file.getAbsolutePath());
        }
        Properties properties = new Properties();
        FileInputStream inputStream = new FileInputStream(configFile);
        try {
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            logger.error("load jproxy.conf fail", e);
            throw e;
        } finally {
            inputStream.close();
        }
    }

    public void close() {
        if (proxyServer != null) {
            proxyServer.abort();
        }
        if (socksServer != null) {
            socksServer.stop();
        }
    }
}
