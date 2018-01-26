package com.szxb.model.minterface;

import java.util.Map;

/**
 * Created by 斩断三千烦恼丝 on 2017/9/12.
 */

public interface IConfig {

    //设置配置
    void setConfig(Map map);

    //获取
    String getConfig(String paraName);
}
