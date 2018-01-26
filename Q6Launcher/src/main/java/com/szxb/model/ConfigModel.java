package com.szxb.model;

import com.szxb.model.minterface.IConfig;

import java.util.Map;

/**
 * Created by 斩断三千烦恼丝 on 2017/9/12.
 */

public class ConfigModel implements IConfig {
    private ConfigModel() {
    }

    private Map map;

    public static ConfigModel getInstance() {
        return getExample.config;
    }

    @Override
    public void setConfig(Map map) {
        this.map = map;
    }

    @Override
    public String getConfig(String paraName) {
        if (map == null) {

            return "";
        }
        return map.get(paraName).toString();
    }

    static class getExample {
        static final ConfigModel config = new ConfigModel();
    }


}
