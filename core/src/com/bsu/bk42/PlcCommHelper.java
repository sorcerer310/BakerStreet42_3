package com.bsu.bk42;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PropertiesUtils;

/**
 * PLCͨѶ������
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
     * �򵥵�get����,һ�㲻��Ҫ����ֵ,ֻ����ĳЩһ���Բ���
     * @param path   ��������·��������,��"/"��ͷ
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
     * һ�η��Ͷ���ָ��,ÿ��ָ�����500ms�ӳ�
     * @param path  ���ָ���·��
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