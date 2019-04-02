package com.hyjf.zuul.filter;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.common.bean.AccessToken;
import com.hyjf.common.cache.RedisConstants;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.zuul.contant.GatewayConstant;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @Author walter.limeng
 * @Description //处理请求后置过滤
 * @Date $ $
 **/

public class ReturnFilter extends ZuulFilter {
    private static Logger logger = LoggerFactory.getLogger(ReturnFilter.class);
    @Override
    public Object run() {

        try{
            RequestContext ctx = RequestContext.getCurrentContext();
            InputStream stream = ctx.getResponseDataStream();
            String body = StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
            String originalRequestPath = ctx.get(FilterConstants.REQUEST_URI_KEY).toString();
            HttpServletRequest request = ctx.getRequest();

            if(originalRequestPath.contains(GatewayConstant.WEB_CHANNEL)){
                String token = "";
                token = request.getHeader(GatewayConstant.TOKEN);
                if (StringUtils.isNotBlank(body)) {
                    if (StringUtils.isBlank(token)) {
                        logger.warn("user is not exist, token is : {}...", token);
                        body = setResult(body);
                    }else{
                        AccessToken accessToken = RedisUtils.getObj(RedisConstants.USER_TOEKN_KEY + token, AccessToken.class);

                        if (accessToken == null) {
                            logger.warn("user is not exist, token is : {}...", token);
                            body = setResult(body);
                        }
                    }
                }
                ctx.setResponseBody(body);

//            ctx.setSendZuulResponse(true);
                ctx.setResponseStatusCode(200);
                ctx.setResponseBody(body);// 输出最终结果
            }

        }catch (Exception e){

        }

        return null;
    }

    private String setResult(String body){
        JSONObject  jasonObject = JSONObject.parseObject(body);
        Map result = (Map)jasonObject;
        result.put("islogined", "0");
        body = result.toString();
        return body;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public String filterType() {
        return "post";// 在请求被处理之后，会进入该过滤器
    }
}

