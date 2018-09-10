package com.hyjf.pay.lib.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author xiasq
 * @version FddSystemConfig, v0.1 2018/9/10 14:40
 */
@Component
public class FddSystemConfig {

    @Value("${hyjf.pay.fdd.url}")
    public String fddUrl;

    public String getFddUrl() {
        return fddUrl;
    }

    public void setFddUrl(String fddUrl) {
        this.fddUrl = fddUrl;
    }
}
