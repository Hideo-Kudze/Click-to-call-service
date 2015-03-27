package com.HideoKuzeGits.Callback.requestReplyWebsocetWrap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 13.02.15.
 */
public class RequestsMap<T> {

    private Map<String, Map<String, T>> innerMap = new HashMap<String, Map<String, T>>();


    public void put(String destination, String id, T obj) {

        Map<String, T> idObjMap = innerMap.get(destination);

        if (idObjMap == null) {
            idObjMap = new HashMap<String, T>();
            innerMap.put(destination, idObjMap);
        }

        idObjMap.put(id, obj);
    }

    public T get(String destination, String id) {

        Map<String, T> idObjMap = innerMap.get(destination);
        if (idObjMap == null) return null;
        return idObjMap.get(id);

    }

    public T remove(String destination, String id) {

        Map<String, T> idObjMap = innerMap.get(destination);
        if (idObjMap == null) return null;
        return idObjMap.remove(id);
    }

}
