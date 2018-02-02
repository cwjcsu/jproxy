package com.cwjcsu.jproxy.socks;

import java.net.PasswordAuthentication;

public class SocksConfig {

    //server => remote target config
    private int targetWriteBufferLowWaterMark = 32768;
    private int targetWriteBufferHighWaterMark = 65536;
    protected int targetTcpSendBufferSize = 65536;
    protected int targetTcpReceiveBufferSize = 4864;
    protected int targetAdaptiveReceiveBufferMinSize = 64;
    protected int targetAdaptiveReceiveBufferIniSize = 512;
    protected int targetAdaptiveReceiveBufferMaxSize = 1024;
    protected int connectTimeout = 10 * 1000;
    protected PasswordAuthentication authentication;
    protected String localBindAddress = "0.0.0.0";//bind address for remote target
    private int adaptiveReceiveBufferMinSize = 64;
    private int adaptiveReceiveBufferIniSize = 1024;
    private int adaptiveReceiveBufferMaxSize = 65536;


    public int getTargetTcpSendBufferSize() {
        return targetTcpSendBufferSize;
    }

    public void setTargetTcpSendBufferSize(int targetTcpSendBufferSize) {
        this.targetTcpSendBufferSize = targetTcpSendBufferSize;
    }

    public int getTargetTcpReceiveBufferSize() {
        return targetTcpReceiveBufferSize;
    }

    public void setTargetTcpReceiveBufferSize(int targetTcpReceiveBufferSize) {
        this.targetTcpReceiveBufferSize = targetTcpReceiveBufferSize;
    }

    public int getTargetAdaptiveReceiveBufferMinSize() {
        return targetAdaptiveReceiveBufferMinSize;
    }

    public void setTargetAdaptiveReceiveBufferMinSize(int targetAdaptiveReceiveBufferMinSize) {
        this.targetAdaptiveReceiveBufferMinSize = targetAdaptiveReceiveBufferMinSize;
    }

    public int getTargetAdaptiveReceiveBufferIniSize() {
        return targetAdaptiveReceiveBufferIniSize;
    }

    public void setTargetAdaptiveReceiveBufferIniSize(int targetAdaptiveReceiveBufferIniSize) {
        this.targetAdaptiveReceiveBufferIniSize = targetAdaptiveReceiveBufferIniSize;
    }

    public int getTargetAdaptiveReceiveBufferMaxSize() {
        return targetAdaptiveReceiveBufferMaxSize;
    }

    public void setTargetAdaptiveReceiveBufferMaxSize(int targetAdaptiveReceiveBufferMaxSize) {
        this.targetAdaptiveReceiveBufferMaxSize = targetAdaptiveReceiveBufferMaxSize;
    }

    public PasswordAuthentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(PasswordAuthentication authentication) {
        this.authentication = authentication;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getLocalBindAddress() {
        return localBindAddress;
    }

    public void setLocalBindAddress(String localBindAddress) {
        this.localBindAddress = localBindAddress;
    }


    public int getAdaptiveReceiveBufferMinSize() {
        return adaptiveReceiveBufferMinSize;
    }

    public void setAdaptiveReceiveBufferMinSize(int adaptiveReceiveBufferMinSize) {
        this.adaptiveReceiveBufferMinSize = adaptiveReceiveBufferMinSize;
    }

    public int getAdaptiveReceiveBufferIniSize() {
        return adaptiveReceiveBufferIniSize;
    }

    public void setAdaptiveReceiveBufferIniSize(int adaptiveReceiveBufferIniSize) {
        this.adaptiveReceiveBufferIniSize = adaptiveReceiveBufferIniSize;
    }

    public int getAdaptiveReceiveBufferMaxSize() {
        return adaptiveReceiveBufferMaxSize;
    }

    public void setAdaptiveReceiveBufferMaxSize(int adaptiveReceiveBufferMaxSize) {
        this.adaptiveReceiveBufferMaxSize = adaptiveReceiveBufferMaxSize;
    }

    public int getTargetWriteBufferLowWaterMark() {
        return targetWriteBufferLowWaterMark;
    }

    public void setTargetWriteBufferLowWaterMark(int targetWriteBufferLowWaterMark) {
        this.targetWriteBufferLowWaterMark = targetWriteBufferLowWaterMark;
    }

    public int getTargetWriteBufferHighWaterMark() {
        return targetWriteBufferHighWaterMark;
    }

    public void setTargetWriteBufferHighWaterMark(int targetWriteBufferHighWaterMark) {
        this.targetWriteBufferHighWaterMark = targetWriteBufferHighWaterMark;
    }
}
