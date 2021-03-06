package com.hyjf.cs.message.service.hgreportdata.caijing.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hyjf.am.resquest.message.CACustomerRequest;
import com.hyjf.am.resquest.admin.CaiJingLogRequest;
import com.hyjf.am.resquest.user.CertificateAuthorityRequest;
import com.hyjf.am.resquest.user.LoanSubjectCertificateAuthorityRequest;
import com.hyjf.am.vo.hgreportdata.caijing.ZeroOneBorrowDataVO;
import com.hyjf.am.vo.hgreportdata.caijing.ZeroOneDataVO;
import com.hyjf.am.vo.trade.borrow.BorrowAndInfoVO;
import com.hyjf.am.vo.trade.borrow.BorrowManinfoVO;
import com.hyjf.am.vo.trade.borrow.BorrowUserVO;
import com.hyjf.am.vo.user.CertificateAuthorityVO;
import com.hyjf.am.vo.user.LoanSubjectCertificateAuthorityVO;
import com.hyjf.am.vo.user.UserInfoVO;
import com.hyjf.am.vo.user.UserVO;
import com.hyjf.common.enums.ZeroOneCaiJingEnum;
import com.hyjf.common.http.HttpDeal;
import com.hyjf.common.security.util.MD5;
import com.hyjf.common.util.CommonUtils;
import com.hyjf.common.util.CustomUtil;
import com.hyjf.common.util.GetDate;
import com.hyjf.common.validator.Validator;
import com.hyjf.cs.message.bean.hgreportdata.caijing.CaiJingPresentationLog;
import com.hyjf.cs.message.bean.hgreportdata.caijing.ZeroOneBorrowEntity;
import com.hyjf.cs.message.bean.hgreportdata.caijing.ZeroOneDataEntity;
import com.hyjf.cs.message.bean.hgreportdata.caijing.ZeroOneResponse;
import com.hyjf.cs.message.client.AmTradeClient;
import com.hyjf.cs.message.client.AmUserClient;
import com.hyjf.cs.message.service.hgreportdata.caijing.CaiJingPresentationLogService;
import com.hyjf.cs.message.service.hgreportdata.caijing.ZeroOneCaiJingService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @Author: yinhui
 * @Date: 2019/6/3 10:33
 * @Version 1.0
 */
@Service
public class ZeroOneCaiJingServiceImpl implements ZeroOneCaiJingService {

    private final Logger logger = LoggerFactory.getLogger(ZeroOneCaiJingServiceImpl.class);

    private static final String INVESTRECORD = "出借记录";
    private static final String BORROWRECORD = "借款记录";
    private static final String ADVANCEREPAY = "提前还款";
    private static final int defaultRowMaxCount = 1500;//最大行数

    @Autowired
    private AmTradeClient amTradeClient;

    @Autowired
    private AmUserClient amUserClient;

    @Autowired
    private CaiJingPresentationLogService presentationLogService;

    public static String sendUrl = "";

    @Value("${01caijing.send.url}")
    public void setSendUrl(String caijingsendUrl) {
        ZeroOneCaiJingServiceImpl.sendUrl = caijingsendUrl;
    }

    /**
     * 借款记录接口报送
     */
    @Override
    public void borrowRecordSub(String startDate, String endDate) {
        logger.info("借款记录接口报送开始");
        String dateStart = GetDate.dataformat(startDate, GetDate.date_sdf_key);
        String dateEnd = GetDate.dataformat(endDate, GetDate.date_sdf_key);

        logger.info("借款记录接口查询开始时间：" + dateStart+ ",结束时间：" + dateEnd);
        List<ZeroOneBorrowDataVO> borrowDataVOList = amTradeClient.queryBorrowRecordSub(dateStart, dateEnd);
        if (CollectionUtils.isEmpty(borrowDataVOList)) {
            logger.info("借款记录接口报送数据为空");
            return;
        }


        logger.info("借款记录接口报送条数=" + borrowDataVOList.size());

        Map<Integer, String> mapUserId = queryCACustomerId(borrowDataVOList);
        if (mapUserId == null || mapUserId.size() == 0) {
            logger.info("借款记录接口报送结束,报送用户id为空");
            return;
        }

        for (ZeroOneBorrowDataVO voList : borrowDataVOList) {
            if (mapUserId.get(Integer.valueOf(voList.getUserid())) != null) {
                voList.setUsername(mapUserId.get(Integer.valueOf(voList.getUserid())));
                voList.setUserid(mapUserId.get(Integer.valueOf(voList.getUserid())));
            } else {
                logger.info("借款记录接口报送 当前用户编号为空,userId=" + voList.getUserid());
                voList.setUserid(null);
                voList.setUsername(null);
            }
            voList.setId(CustomUtil.nidSign(voList.getId()));
        }

        List<ZeroOneBorrowEntity> borrowEntities = CommonUtils.convertBeanList(borrowDataVOList, ZeroOneBorrowEntity.class);

        //将数据大于1500的进行拆分上报
        int totalCount = borrowEntities.size();
        int sheetCount = (totalCount % defaultRowMaxCount) == 0 ? totalCount / defaultRowMaxCount
                : totalCount / defaultRowMaxCount + 1;

        for (int i = 1; i <= sheetCount; i++) {
            Integer status = 0;
            String description = "";

            int start=(i-1) * defaultRowMaxCount;
            int end = Math.min(totalCount, i * defaultRowMaxCount);

            if(totalCount >= i){
                List<ZeroOneBorrowEntity> result=borrowEntities.subList(start,end);

                Map<String,Object> map = new HashMap<>();
                map.put("data",result);
                ZeroOneResponse zeroOneResponse = sendDataReport(ZeroOneCaiJingEnum.LEND.getName(), JSONObject.toJSONString(map, SerializerFeature.WriteMapNullValue));
                if (zeroOneResponse != null && zeroOneResponse.result_code == 1) {
                    //报送成功
                    logger.info("借款记录接口报送成功,预计报送:{},实际报送:{}",result.size(),zeroOneResponse.data_num);
                    status = 1;
                } else {
                    logger.info("借款记录接口报送失败");
                    status = 0;
                    if(zeroOneResponse != null && zeroOneResponse.result_msg != null){
                        description = zeroOneResponse.result_msg;
                    }
                }
                //插入mongo表
                this.saveLog(BORROWRECORD,result.size(),dateStart,dateEnd,status,description,map);
            }
        }
        logger.info("借款记录接口报送结束");
    }

    /**
     * 投资记录接口报送
     */
    @Override
    public void investRecordSub(String startDate, String endDate) {
        logger.info("投资记录接口报送开始");

        startDate = GetDate.dataformat(startDate, GetDate.date_sdf_key);
        endDate = GetDate.dataformat(endDate, GetDate.date_sdf_key);

        List<ZeroOneDataVO> zeroOneDataVOList = amTradeClient.queryInvestRecordSub(startDate, endDate);

        if (zeroOneDataVOList == null || zeroOneDataVOList.size() == 0) {
            logger.info("投资记录接口无数据报送结束");
            return;
        }

        logger.info("投资记录接口报送条数=" + zeroOneDataVOList.size());

        Set<Integer> listUserId = new HashSet<>();
        for (ZeroOneDataVO voList : zeroOneDataVOList) {
            listUserId.add(voList.getUserIds());
        }

        Map<Integer, String> mapUserId = queryCustomerId(listUserId);
        if (mapUserId == null || mapUserId.size() == 0) {
            logger.info("投资记录接口报送结束,报送用户id为空");
            return;
        }

        for (ZeroOneDataVO voList : zeroOneDataVOList) {
            if (mapUserId.get(voList.getUserIds()) != null) {
                voList.setUsername(mapUserId.get(voList.getUserIds()));
                voList.setUserid(mapUserId.get(voList.getUserIds()));
            } else {
                logger.info("投资记录接口报送 当前用户编号为空,userId=" + voList.getUserIds());
                voList.setUserid(null);
                voList.setUsername(null);
            }

        }

        List<ZeroOneDataEntity> list = CommonUtils.convertBeanList(zeroOneDataVOList, ZeroOneDataEntity.class);

        //将数据大于1500的进行拆分上报
        int totalCount = list.size();
        int sheetCount = (totalCount % defaultRowMaxCount) == 0 ? totalCount / defaultRowMaxCount
                : totalCount / defaultRowMaxCount + 1;

        for (int i = 1; i <= sheetCount; i++) {
            Integer status = 0;
            String description = "";

            int start = (i - 1) * defaultRowMaxCount;
            int end = Math.min(totalCount, i * defaultRowMaxCount);

            if (totalCount >= i) {
                List<ZeroOneDataEntity> result = list.subList(start, end);

                Map<String,Object> map = new HashMap<>();
                map.put("data",result);
                ZeroOneResponse zeroOneResponse = sendDataReport(ZeroOneCaiJingEnum.INVEST.getName(),JSONObject.toJSONString(map));
                if (zeroOneResponse != null && zeroOneResponse.result_code == 1) {
                    //报送成功
                    logger.info("投资记录接口报送成功,预计报送:{},实际报送:{}",result.size(),zeroOneResponse.data_num);
                    status = 1;
                } else {
                    logger.info("投资记录接口报送失败");
                    status = 0;
                    if(zeroOneResponse != null && zeroOneResponse.result_msg != null){
                        description = zeroOneResponse.result_msg;
                    }
                }
                //插入mongo表
                this.saveLog(INVESTRECORD,result.size(),startDate,endDate,status,description,map);
            }
        }

        logger.info("投资记录接口报送结束");
    }


    /**
     * 提前还款接口报送
     */
    @Override
    public void advancedRepay(String startDate, String endDate) {
        logger.info("提前还款接口报送开始");
        startDate = GetDate.dataformat(startDate, GetDate.date_sdf_key);
        endDate = GetDate.dataformat(endDate, GetDate.date_sdf_key);

        List<ZeroOneDataVO> zeroOneDataVOList = amTradeClient.queryAdvancedRepay(startDate, endDate);
        if (zeroOneDataVOList == null || zeroOneDataVOList.size() == 0) {
            logger.info("提前还款接口无数据报送结束");
            return;
        }

        List<ZeroOneDataEntity> list = CommonUtils.convertBeanList(zeroOneDataVOList, ZeroOneDataEntity.class);

        //将数据大于1500的进行拆分上报
        int totalCount = list.size();
        int sheetCount = (totalCount % defaultRowMaxCount) == 0 ? totalCount / defaultRowMaxCount
                : totalCount / defaultRowMaxCount + 1;

        for (int i = 1; i <= sheetCount; i++) {
            Integer status = 0;
            String description = "";
            int start = (i - 1) * defaultRowMaxCount;
            int end = Math.min(totalCount, i * defaultRowMaxCount);

            if (totalCount >= i) {
                List<ZeroOneDataEntity> result = list.subList(start, end);

                Map<String,Object> map = new HashMap<>();
                map.put("data",result);
                ZeroOneResponse zeroOneResponse = sendDataReport(ZeroOneCaiJingEnum.ADVANCEDREPAY.getName(), JSONObject.toJSONString(map));
                if (zeroOneResponse != null && zeroOneResponse.result_code == 1) {
                    //报送成功
                    logger.info("提前还款接口报送成功,预计报送:{},实际报送:{}",result.size(),zeroOneResponse.data_num);
                    status = 1;
                } else {
                    logger.info("提前还款接口报送失败");
                    status = 0;
                    if(zeroOneResponse != null && zeroOneResponse.result_msg != null){
                        description = zeroOneResponse.result_msg;
                    }
                }
                //插入mongo表
                this.saveLog(ADVANCEREPAY,result.size(),startDate,endDate,status,description,map);
            }
        }

        logger.info("提前还款接口报送结束");
    }

    /**
     * 获取借款用户编号
     *
     * @param borrowDataVOList
     * @return
     */
    private Map<Integer, String> queryCACustomerId(List<ZeroOneBorrowDataVO> borrowDataVOList) {
        //智投
        List<Integer> userIds = new ArrayList<>();
        //企业
        List<String> borrowNids = new ArrayList<>();
        //个人
        List<String> nids = new ArrayList<>();
        //借款主体。key = borrowNid or SocialCreditCode or CardNo
        Map<String,Integer> borrowMain = new HashMap<>();

        //拆分
        for (ZeroOneBorrowDataVO borrowDataVO : borrowDataVOList) {
            if (StringUtils.isNotBlank(borrowDataVO.getPlanNid())) {
                userIds.add(Integer.valueOf(borrowDataVO.getUserid()));
            } else {
                if (borrowDataVO.getCompanyOrPersonal() != null && borrowDataVO.getCompanyOrPersonal() == 1) {
                    borrowNids.add(borrowDataVO.getId());
                }
                if (borrowDataVO.getCompanyOrPersonal() != null && borrowDataVO.getCompanyOrPersonal() == 2 || borrowDataVO.getCompanyOrPersonal() == 0) {
                    nids.add(borrowDataVO.getId());
                }
                borrowMain.put(borrowDataVO.getId(),Integer.valueOf(borrowDataVO.getUserid()));
            }
        }

        //智投计划的用户，属于第三方个人的，直接查询CA认证客户编号
        Map<Integer, String> mapUserId = new HashMap<>();
        if (!CollectionUtils.isEmpty(userIds)) {
            List<CertificateAuthorityVO> voList = amUserClient.queryCustomerId(userIds);
            if (!CollectionUtils.isEmpty(voList)) {
                for (CertificateAuthorityVO authorityVO : voList) {
                    mapUserId.put(authorityVO.getUserId(), authorityVO.getCustomerId());
                }
            }
        }

        //企业CA认证客户编号
        if (!CollectionUtils.isEmpty(borrowNids)) {
            List<BorrowUserVO> userVOS = amTradeClient.getBorrowUserInfo(borrowNids);
            if (!CollectionUtils.isEmpty(userVOS)) {
                List<String> idNoList = new ArrayList<>(userVOS.size());
                for (BorrowUserVO borrowUsers : userVOS) {
                    idNoList.add(borrowUsers.getSocialCreditCode());
                    if(borrowMain.get(borrowUsers.getBorrowNid()) != null){
                        borrowMain.put(borrowUsers.getSocialCreditCode(),borrowMain.get(borrowUsers.getBorrowNid()));
                    }
                }
                mapUserId.putAll(getCACustomerID(borrowMain,idNoList,1));
            }
        }

        //个人CA认证客户编号
        if (!CollectionUtils.isEmpty(nids)) {
            List<BorrowManinfoVO> maninfoVOList = amTradeClient.getBorrowManList(nids);
            if (!CollectionUtils.isEmpty(maninfoVOList)) {
                List<String> cardList = new ArrayList<>(maninfoVOList.size());
                for (BorrowManinfoVO borrowManinfoVO : maninfoVOList) {
                    cardList.add(borrowManinfoVO.getCardNo());
                    if(borrowMain.get(borrowManinfoVO.getBorrowNid()) != null){
                        borrowMain.put(borrowManinfoVO.getCardNo(),borrowMain.get(borrowManinfoVO.getBorrowNid()));
                    }
                }
                mapUserId.putAll(getCACustomerID(borrowMain,cardList,0));
            }
        }
        return mapUserId;
    }

    /**
     * 批量获取借款主体为企业的CA认证客户编号
     * @return
     */
    private Map<Integer, String> getCACustomerID(Map<String,Integer> borrowMain, List<String> idNoList , Integer idType) {
        Map<Integer, String> caCustomerIds = new HashMap<>();
        if (CollectionUtils.isEmpty(idNoList)) {
            return caCustomerIds;
        }

        CACustomerRequest request = new CACustomerRequest();
        request.setIdType(idType);
        request.setIdNoList(idNoList);

        List<LoanSubjectCertificateAuthorityVO> authorityVOList = amUserClient.getSubjectCertificateAuthorityList(request);
        if (!CollectionUtils.isEmpty(authorityVOList)) {

            for(LoanSubjectCertificateAuthorityVO vo : authorityVOList){
                if(borrowMain.get(vo.getIdNo()) != null){
                    //转换为借款主体的用户ID
                    caCustomerIds.put(borrowMain.get(vo.getIdNo()), vo.getCustomerId());
                }else{
                    caCustomerIds.put(vo.getUserId(), vo.getCustomerId());
                }

            }
        }

        return caCustomerIds;
    }

    /**
     * 获得用户编号
     *
     * @param userId
     * @return
     */
    private Map<Integer, String> queryCustomerId(Set<Integer> userId) {
        if (userId == null || userId.size() == 0) {
            return null;
        }
        Map<Integer, String> mapUserId = new HashMap<>();

        List<CertificateAuthorityVO> voList = amUserClient.queryCustomerId(userId);
        for (CertificateAuthorityVO vo : voList) {
            mapUserId.put(vo.getUserId(), vo.getCustomerId());
        }
        return mapUserId;
    }

    /**
     * 保存零壹财经报送数据
     * @param size 报送数据条数
     * @param dateStart 报送开始时间
     * @param dateEnd   报送结束时间
     * @param status   报送状态，1=成功，0=失败
     * @param description 报送返回描述
     * @param jsonData  报送数据，json格式
     */
    private void saveLog(String logType,Integer size,String dateStart, String dateEnd,Integer status,String description,
                         Map<String,Object> jsonData){
        CaiJingPresentationLog presentationLog = new CaiJingPresentationLog();
        presentationLog.setLogType(logType);
        presentationLog.setCount(size);
        presentationLog.setStartDate(dateStart);
        presentationLog.setEndDate(dateEnd);

        presentationLog.setStatus(status);
        presentationLog.setDescription(description);
        presentationLog.setJson(jsonData);
        this.presentationLogService.insertLog(presentationLog);
    }

    /**
     * 数据推送
     *
     * @param type 类型，借款=lend ,出借=invest ,提前还款=advanced-repay
     * @param json 数据json
     * @return
     */
    private ZeroOneResponse sendDataReport(String type, String json) {

        StringBuilder stbuUrl = new StringBuilder();
        stbuUrl.append(sendUrl);
        stbuUrl.append(type);
        stbuUrl.append("/commit.json");

        Map<String, String> sendMap = new HashMap<>();
        ZeroOneResponse zeroOneResponse = null;
        try {
            sendMap.put("visit_key", "www.hyjf.com");
            sendMap.put("time", String.valueOf(System.currentTimeMillis()));
            sendMap.put("data", json);

            //Sige md5(密钥&visit_key=域名&time=时间戳&data=data字符串的byte长度)
            StringBuilder stringBuilder = new StringBuilder();
            //生产密钥
            stringBuilder.append("5A25DD85BD76829D537C7E59AA5DA766");
            stringBuilder.append("&visit_key=");
            stringBuilder.append(sendMap.get("visit_key"));
            stringBuilder.append("&time=");
            stringBuilder.append(sendMap.get("time"));
            stringBuilder.append("&data=");
            stringBuilder.append(json.getBytes().length);

            String sign = MD5.toMD5CodeCaps(stringBuilder.toString());

            sendMap.put("sign", sign);

            //Post请求
            String response = HttpDeal.post(stbuUrl.toString(), sendMap);

            zeroOneResponse = JSONObject.parseObject(response, ZeroOneResponse.class);
            logger.info("零壹财经数据报送返回结果="+zeroOneResponse);
        } catch (Exception e) {
            logger.error("零壹财经数据报送错误 error:", e);
        }

        return zeroOneResponse;
    }

}
