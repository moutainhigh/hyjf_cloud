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

        RequestContext ctx = RequestContext.getCurrentContext();

        try{

            HttpServletRequest request = ctx.getRequest();
            String token = "";
            token = request.getHeader(GatewayConstant.TOKEN);
            String originalRequestPath = ctx.get(FilterConstants.REQUEST_URI_KEY).toString();
            if (!StringUtils.isBlank(token)) {
                logger.warn("originalRequestPath: {}...", originalRequestPath);
                AccessToken accessToken = RedisUtils.getObj(RedisConstants.USER_TOEKN_KEY + token, AccessToken.class);
                if (accessToken == null) {
                    InputStream stream = ctx.getResponseDataStream();
                    String body = StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
                    logger.warn("user is not exist, token is : {}...，Redis上未查询到该用户登陆信息", token);
                    body = setResult(body);
                    if(StringUtils.isBlank(body)){
                        body = ctx.getResponseBody();
                    }
                    ctx.setResponseBody(body);// 输出最终结果
                }
            }

//            if (StringUtils.isBlank(token)) {
//                InputStream stream = ctx.getResponseDataStream();
//                String body = StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
//                logger.warn("originalRequestPath: {}...", originalRequestPath);
//                logger.warn("user is not exist, token is : {}...", token);
//                body = setResult(body);
//                ctx.setResponseBody(body);// 输出最终结果
//            }else{
//                AccessToken accessToken = RedisUtils.getObj(RedisConstants.USER_TOEKN_KEY + token, AccessToken.class);
//
//                if (accessToken == null) {
//                    InputStream stream = ctx.getResponseDataStream();
//                    String body = StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
//                    logger.warn("user is not exist, token is : {}...，Redis上未查询到该用户登陆信息", token);
//                    body = setResult(body);
//                    ctx.setResponseBody(body);// 输出最终结果
//                }
//            }
            logger.info("token is :{}",token);
        }catch (IOException e){
            logger.error("获取业务端返回body异常，不进行处理");
        }

        return null;
    }

    private String setResult(String body){
        try{
            JSONObject  jasonObject = JSONObject.parseObject(body);
            Map result = (Map)jasonObject;
            result.put("islogined", "0");
            body = result.toString();
        }catch (Exception e){
            logger.info("body:{}",body);
            logger.error("业务端返回为boolean或者其他无法识别类型，不进行处理");
        }
        logger.info("body:{}",body);

        return body;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String originalRequestPath = ctx.get(FilterConstants.REQUEST_URI_KEY).toString();
        //只处理PC端返回
        if(originalRequestPath.contains(GatewayConstant.WEB_CHANNEL)) {
            return true;
        }else{
            return false;
        }
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

