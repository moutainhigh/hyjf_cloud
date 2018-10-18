package com.hyjf.pay.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyjf.common.constants.MQConstant;
import com.hyjf.common.validator.Validator;
import com.hyjf.pay.base.BaseController;
import com.hyjf.pay.bean.DzqzCallDefine;
import com.hyjf.pay.config.SystemConfig;
import com.hyjf.pay.lib.fadada.bean.DzqzCallBean;
import com.hyjf.pay.lib.fadada.call.DzqzCallApi;
import com.hyjf.pay.lib.fadada.call.impl.DzqzCallApiImpl;
import com.hyjf.pay.mq.FddProducer;
import com.hyjf.pay.mq.Producer;
import com.hyjf.pay.service.DzqzPayLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.TreeMap;

@RestController
@RequestMapping(DzqzCallDefine.FDD_REQUEST_MAPPING)
public class DzqzCallController extends BaseController {

    @Autowired
    private DzqzPayLogService logService;

    @Autowired
    private FddProducer fddProducer;

    @Autowired
    private Producer producer;

    @Autowired
    SystemConfig systemConfig;


    Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 接口调用（后台）
     * @param bean
     * @return
     * @throws Exception
     */
    @PostMapping(value = DzqzCallDefine.FDD_CALL_APIBG)
    public String callApiBg(@RequestBody DzqzCallBean bean) {
        log.info("--------------开始调用pay工程-------------");
        log.info("-------fdd-------ca参数-----[{}]",JSON.toJSONString(bean));
        String ret = "";
        String orderId = "";
        try {

            bean.setApp_id(systemConfig.getFaaAppUrl());
            bean.setSecret(systemConfig.getFddSecret());
            bean.setV(systemConfig.getFddVersion());
            bean.setUrl(systemConfig.getFddUrl());

            bean.setAllParams(new TreeMap<String, String>());
            // 参数转换成Map
            bean.convert();
            // 消息类型
            String txCode = bean.getTxCode();
            orderId = bean.getLogordid();
            if (Validator.isNull(txCode)) {
                throw new RuntimeException("消息类型不能为空");
            }
            if (Validator.isNull(bean.getUserId())) {
                throw new RuntimeException("用户ID不能为空");
            }
            // 保存发送日志
            logService.saveDzqzPaySendLog(bean);
            // 得到接口API对象
            DzqzCallApi api = new DzqzCallApiImpl();
            Class<DzqzCallApiImpl> c = DzqzCallApiImpl.class;
            Object obj = api;
            // 取得该消息类型对应的bean
            Method method = c.getMethod(txCode, DzqzCallBean.class);
            Object retBean = method.invoke(obj, bean);
            if (retBean != null) {
                String result = (String) retBean;
                ret = result;
                DzqzCallBean dzqzCallBean = JSONObject.parseObject(result, DzqzCallBean.class);
                dzqzCallBean.setLogordid(orderId);
                dzqzCallBean.setTxDate(bean.getTxDate());
                dzqzCallBean.setTxTime(bean.getTxTime());
                logService.saveDzqzPayReturnLog(dzqzCallBean);
            }
        } catch (Exception e) {
            log.info("---------------调用法大大接口异常，txcode:" + bean.getTxCode() + ",logordid:" + orderId);
            log.error("法大大接口异常:【{}】",e);
        } finally {
            log.info("---------------调用法大大接口完毕，txcode:" + bean.getTxCode() + ",logordid:" + orderId);
        }
        return ret;
    }

    /**
     * 合同签署异步返回（后台）
     * @param request
     * @param bean
     * @return
     * @throws Exception
     */
    @PostMapping(value = DzqzCallDefine.FDD_CALL_API_SIGNNODIFY)
    public String callApiSignNodify(HttpServletRequest request, @ModelAttribute DzqzCallBean bean) throws Exception {
        String orderId = bean.getTransaction_id();
        String transType = request.getParameter("transType");
        bean.setTransType(Integer.valueOf(transType));
        log.info("--------------开始接收合同签署异步返回-----------交易号：" + orderId + "--");
        try {
            String result_code = bean.getResult_code();
            if (!"3000".equals(result_code)){//未签署成功
                log.info("--------------合同签署异步返回签署失败-----------交易号：" + orderId + "--交易描述：" + bean.getResult_desc());
            }else{
                fddProducer.messageSend(new Producer.MassageContent(MQConstant.FDD_TOPIC,MQConstant.FDD_AUTO_SIGN_TAG,JSON.toJSONBytes(bean)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("---------------合同签署异步返回异常，txcode:" + bean.getResult_code() + ",logordid:" + orderId);
        }
        return "success";
    }

}
