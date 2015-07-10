package com.bsu.bk42;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PropertiesUtils;

/**
 * PLC通讯帮助类
 * Created by Administrator on 2015/7/3.
 */
public class PlcCommHelper {
    private int senddelay = 100;
    private static PlcCommHelper instance = null;
    public static PlcCommHelper getInstance(){
        if(instance ==null)
            instance = new PlcCommHelper();
        return instance;
    }
    private ObjectMap<String,String> netcfg = new ObjectMap<String,String>();
    private PlcCommHelper(){
        try{
            PropertiesUtils.load(netcfg, Gdx.files.internal("net.properties").reader());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 简单的get请求,一般不需要返回值,只用于某些一次性操作
     * @param path   不带域名路径及参数,以"/"开头
     */
    public void simpleGet(String path){
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        request.setUrl(netcfg.get("urlpath")+path);
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {}
            @Override
            public void failed(Throwable t) {}
            @Override
            public void cancelled() {}
        });
    }

    /**
     * 一次发送多条指令,每条指令间有500ms延迟
     * @param path  多个指令的路径
     */
    public void simpleGetMoreCmd(Array<String> path){
        for(String p:path){
            Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
            request.setUrl(netcfg.get("urlpath")+p);
            Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
                @Override
                public void handleHttpResponse(Net.HttpResponse httpResponse) {}
                @Override
                public void failed(Throwable t) {}
                @Override
                public void cancelled() {}
            });

            try {
                Thread.sleep(senddelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
