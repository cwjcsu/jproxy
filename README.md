# A simple Socks Server & http proxy Server

This simple service provide an authentication based on user and password. Written in Java,based on Netty 4.x. It's wrappered as a service using [java service wrapper](http://www.tanukisoftware.com/en/index.php)

## Install
1. git clone this project.
2. use `mvn isntall`
3. you will see 3 packages in `/target`
```
-rw-rw-r-- 1 atlas atlas 8813109 Feb  3 06:54 jproxy-linux32.tar.gz
-rw-rw-r-- 1 atlas atlas 8832092 Feb  3 06:54 jproxy-linux64.tar.gz
-rw-rw-r-- 1 atlas atlas 9009963 Feb  3 06:54 jproxy-windows.zip
```
4. you can install them any where,for example:
```
#tar -xvf jproxy-linux64.tar.gz -C /usr/local/
```
5. set your Java home
```
#vim /usr/local/jproxy/wrapper/conf/wrapper.conf
 set.JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.161-0.b14.el7_4.x86_64/jre
```
6. change default username & password & the binded port.
```
# vim /usr/local/jproxy/conf/jproxy.conf
http.enable=true
http.user=YourhttpProxyUserName
http.password=YourHttpProxyPwd
http.port=9521
http.host=0.0.0.0

socks.enable=true
socks.user=YourSocks5ProxyUserName
socks.password=YourSocks5ProxyPassword
socks.port=9522
socks.host=0.0.0.0
```
You can use another port and host,if you set `http.host=127.0.0.1` or `socks.host=127.0.0.1` than the http proxy or socks proxy can only be connected from local host.

7. install as a service and start
```
#/usr/local/jproxy/wrapper/bin/jproxy.sh installstart
```
