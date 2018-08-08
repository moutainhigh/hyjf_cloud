package com.hyjf.admin.client.impl;

import com.hyjf.admin.client.AmConfigClient;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.admin.BankConfigResponse;
import com.hyjf.am.response.admin.JxBankConfigResponse;
import com.hyjf.am.response.config.*;
import com.hyjf.am.response.trade.BankReturnCodeConfigResponse;
import com.hyjf.am.response.trade.BanksConfigResponse;
import com.hyjf.am.response.user.MspApplytResponse;
import com.hyjf.am.response.user.MspResponse;
import com.hyjf.am.resquest.user.MspApplytRequest;
import com.hyjf.am.resquest.user.MspRequest;
import com.hyjf.am.vo.config.AdminSystemVO;
import com.hyjf.am.vo.config.ParamNameVO;
import com.hyjf.am.vo.config.SiteSettingsVO;
import com.hyjf.am.vo.config.SmsMailTemplateVO;
import com.hyjf.am.vo.trade.BankConfigVO;
import com.hyjf.am.vo.trade.BankReturnCodeConfigVO;
import com.hyjf.am.vo.trade.BanksConfigVO;
import com.hyjf.common.validator.Validator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author zhangqingqing
 * @version AmConfigClientImpl, v0.1 2018/4/23 20:01
 */
@Service
public class AmConfigClientImpl implements AmConfigClient {
    private static Logger logger = LoggerFactory.getLogger(AmConfigClientImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 用loginUserId去am-config查询登录的用户信息
     * @auth sunpeikai
     * @param loginUserId 登录用户id
     * @return
     */
    @Override
    public AdminSystemVO getUserInfoById(Integer loginUserId) {
        String url = "http://AM-CONFIG/am-config/adminSystem/get_admin_system_by_userid/" + loginUserId;
        AdminSystemResponse response = restTemplate.getForEntity(url,AdminSystemResponse.class).getBody();
        if(response != null){
            return response.getResult();
        }
        return null;
    }

    @Override
    public List<ParamNameVO> getParamNameList(String nameClass) {
        String url = "http://AM-CONFIG/am-config/config/getParamNameList/" + nameClass;
        ParamNameResponse response = restTemplate.getForEntity(url,ParamNameResponse.class).getBody();
        if (Validator.isNotNull(response)){
            return response.getResultList();
        }
        return null;
    }

    /**
     * 查询邮件配置
     * @auth sunpeikai
     * @param
     * @return
     */
    @Override
    public SiteSettingsVO findSiteSetting() {

        SiteSettingsResponse response = restTemplate
                .getForEntity("http://AM-CONFIG/am-config/siteSettings/findOne/", SiteSettingsResponse.class).getBody();
        if (response != null) {
            return response.getResult();
        }
        return null;
    }

    /**
     * 查询邮件模板
     * @auth sunpeikai
     * @param
     * @return
     */
    @Override
    public SmsMailTemplateVO findSmsMailTemplateByCode(String mailCode) {
        SmsMailTemplateResponse response = restTemplate
                .getForEntity("http://AM-CONFIG/am-config/smsMailTemplate/findSmsMailByCode/" + mailCode,
                        SmsMailTemplateResponse.class)
                .getBody();
        if (response != null) {
            return response.getResult();
        }
        return null;
    }

    /**
     * 根据银行错误码查询错误信息
     * @auth sunpeikai
     * @param retCode 错误码
     * @return
     */
	@Override
	public String getBankRetMsg(String retCode) {
		BankReturnCodeConfigResponse response = restTemplate
				.getForEntity("http://AM-CONFIG/config/getBankReturnCodeConfig/" + retCode,
						BankReturnCodeConfigResponse.class)
				.getBody();
		if (response != null) {
			BankReturnCodeConfigVO vo = response.getResult();
			if (vo == null) {
				return Response.ERROR_MSG;
			}
			return StringUtils.isNotBlank(vo.getRetMsg()) ? vo.getRetMsg() : Response.ERROR_MSG;
		}
		return Response.ERROR_MSG;
	}

    /**
     * 获取银行返回码
     *
     * @param retCode
     * @return
     */
    @Override
    public BankReturnCodeConfigVO getBankReturnCodeConfig(String retCode) {
        String url = "http://AM-CONFIG/am-config/config/getBankReturnCodeConfig/" + retCode;
        BankReturnCodeConfigResponse response = restTemplate.getForEntity(url, BankReturnCodeConfigResponse.class).getBody();
        if (response != null) {
            return response.getResult();
        }
        return null;
    }
    /**
     * 获取银行列表
     * @author nixiaoling
     * @return
     */
    @Override
    public List<BankConfigVO> selectBankConfigList() {
        BankConfigResponse response = restTemplate
                .getForEntity("http://AM-CONFIG/am-config/config/selectBankConfigList", BankConfigResponse.class)
                .getBody();
        if (response != null && Response.SUCCESS.equals(response.getRtn())) {
            return response.getResultList();
        }
        return null;
    }
    /**
     * 根据银行卡号获取bankId
     * @auther: nxl
     * @param cardNo
     * @return
     */
    @Override
    public String queryBankIdByCardNo(String cardNo) {
        String result = restTemplate
                .getForEntity("http://AM-CONFIG/am-config/config/queryBankIdByCardNo/" + cardNo, String.class).getBody();
        return result;
    }

    /**
     * 根据bankId查找江西银行的银行卡配置表
     * @auther: nxl
     * @param bankId
     * @return
     */
    @Override
    public JxBankConfigResponse getJXbankConfigByBankId(int bankId) {
        JxBankConfigResponse response = restTemplate
                .getForEntity("http://AM-CONFIG/am-config/config/getJXbankConfigByBankId/" + bankId, JxBankConfigResponse.class).getBody();
        return response;
    }
	 /**
	 * 合作机构
	 * @return
	 */
	@Override
	public LinkResponse getLinks() {
		LinkResponse response = restTemplate
               .getForEntity("http://AM-CONFIG/am-config/content/contentlinks/getLinks", LinkResponse.class)
               .getBody();
       if (response != null && Response.SUCCESS.equals(response.getRtn())) {
           return response;
       }
       return null;
	}
	/**
	 * 子账户类型 查询
	 */
	@Override
	public ParamNameResponse getNameCd(String code){
		ParamNameResponse amResponse = restTemplate.getForEntity("http://AM-CONFIG/am-config/accountconfig/getNameCd/"+"FLOW_STATUS", ParamNameResponse.class)
                .getBody();
		if (amResponse != null && Response.SUCCESS.equals(amResponse.getRtn())) {
			return amResponse;
		}
		return null;
	}
	/**
	 * 项目申请人是否存在
	 * @param request
	 * @return
	 */
	@Override
	public AdminSystemResponse isExistsApplicant(String applicant) {
		AdminSystemResponse response = restTemplate
               .getForEntity("http://AM-CONFIG/am-config/adminSystem/isexistsapplicant/"+applicant, AdminSystemResponse.class)
               .getBody();
       if (response != null ) {
           return response;
       }
       return null;
	}
	
	@Override
	public MspApplytResponse getRecordList(MspApplytRequest mspApplytRequest) {
		MspApplytResponse mspApplytResponse = restTemplate
				.postForEntity("http://AM-CONFIG/am-user/mspapply/init" ,mspApplytRequest,
						MspApplytResponse.class)
				.getBody();
		if (mspApplytResponse != null) {
			return mspApplytResponse;
		}
		return null;
	}

	@Override
	public MspApplytResponse infoAction() {
		MspApplytResponse mspApplytResponse = restTemplate
				.postForEntity("http://AM-CONFIG/am-user/mspapply/infoAction" ,null,
						MspApplytResponse.class)
				.getBody();
		if (mspApplytResponse != null) {
			return mspApplytResponse;
		}
		return null;
	}

	@Override
	public MspApplytResponse insertAction(MspApplytRequest mspApplytRequest) {
		MspApplytResponse mspApplytResponse = restTemplate
				.postForEntity("http://AM-CONFIG/am-user/mspapply/insertAction" ,mspApplytRequest,
						MspApplytResponse.class)
				.getBody();
		if (mspApplytResponse != null) {
			return mspApplytResponse;
		}
		return null;
	}

	@Override
	public MspApplytResponse updateAction(MspApplytRequest mspApplytRequest) {
		MspApplytResponse mspApplytResponse = restTemplate
				.postForEntity("http://AM-CONFIG/am-user/mspapply/insertAction" ,mspApplytRequest,
						MspApplytResponse.class)
				.getBody();
		if (mspApplytResponse != null) {
			return mspApplytResponse;
		}
		return null;
	}

	@Override
	public MspApplytResponse deleteRecordAction(MspApplytRequest mspApplytRequest) {
		MspApplytResponse mspApplytResponse = restTemplate
				.postForEntity("http://AM-CONFIG/am-user/mspapply/deleteRecordAction" ,mspApplytRequest,
						MspApplytResponse.class)
				.getBody();
		if (mspApplytResponse != null) {
			return mspApplytResponse;
		}
		return null;
	}

	@Override
	public MspApplytResponse validateBeforeAction(MspApplytRequest mspApplytRequest) {
		MspApplytResponse mspApplytResponse = restTemplate
				.postForEntity("http://AM-CONFIG/am-user/mspapply/validateBeforeAction" ,mspApplytRequest,
						MspApplytResponse.class)
				.getBody();
		if (mspApplytResponse != null) {
			return mspApplytResponse;
		}
		return null;
	}

	@Override
	public MspApplytResponse applyInfo(MspApplytRequest mspApplytRequest) {
		MspApplytResponse mspApplytResponse = restTemplate
				.postForEntity("http://AM-CONFIG/am-user/mspapply/applyInfo" ,mspApplytRequest,
						MspApplytResponse.class)
				.getBody();
		if (mspApplytResponse != null) {
			return mspApplytResponse;
		}
		return null;
	}

	@Override
	public MspApplytResponse shareUser(MspApplytRequest mspApplytRequest) {
		MspApplytResponse mspApplytResponse = restTemplate
				.postForEntity("http://AM-CONFIG/am-user/mspapply/shareUser" ,mspApplytRequest,
						MspApplytResponse.class)
				.getBody();
		if (mspApplytResponse != null) {
			return mspApplytResponse;
		}
		return null;
	}

	@Override
	public MspApplytResponse download(MspApplytRequest mspApplytRequest) {
		MspApplytResponse mspApplytResponse = restTemplate
				.postForEntity("http://AM-CONFIG/am-user/mspapply/download" ,mspApplytRequest,
						MspApplytResponse.class)
				.getBody();
		if (mspApplytResponse != null) {
			return mspApplytResponse;
		}
		return null;
	}

	@Override
	public MspResponse searchAction(MspRequest mspRequest) {
		MspResponse mspResponse = restTemplate
				.postForEntity("http://AM-CONFIG/am-user/mspapplyconfigure/searchAction" ,mspRequest,
						MspResponse.class)
				.getBody();
		if (mspResponse != null) {
			return mspResponse;
		}
		return null;
	}

	@Override
	public MspResponse infoAction(MspRequest mspRequest) {
		MspResponse mspResponse = restTemplate
				.postForEntity("http://AM-CONFIG/am-user/mspapplyconfigure/infoAction" ,mspRequest,
						MspResponse.class)
				.getBody();
		if (mspResponse != null) {
			return mspResponse;
		}
		return null;
	}

	@Override
	public MspResponse insertAction(MspRequest mspRequest) {
		MspResponse mspResponse = restTemplate
				.postForEntity("http://AM-CONFIG/am-user/mspapplyconfigure/searchAction" ,mspRequest,
						MspResponse.class)
				.getBody();
		if (mspResponse != null) {
			return mspResponse;
		}
		return null;
	}

	@Override
	public MspResponse updateAction(MspRequest mspRequest) {
		MspResponse mspResponse = restTemplate
				.postForEntity("http://AM-CONFIG/am-user/mspapplyconfigure/insertAction" ,mspRequest,
						MspResponse.class)
				.getBody();
		if (mspResponse != null) {
			return mspResponse;
		}
		return null;
	}

	@Override
	public MspResponse configureNameError(MspRequest mspRequest) {
		MspResponse mspResponse = restTemplate
				.postForEntity("http://AM-CONFIG/am-user/mspapplyconfigure/configureNameError" ,mspRequest,
						MspResponse.class)
				.getBody();
		if (mspResponse != null) {
			return mspResponse;
		}
		return null;
	}

	@Override
	public MspResponse deleteAction(MspRequest mspRequest) {
		 MspResponse mspResponse = restTemplate
				.postForEntity("http://AM-CONFIG/am-user/mspapplyconfigure/deleteAction" ,mspRequest,
						MspResponse.class)
				.getBody();
		if (mspResponse != null) {
			return mspResponse;
		}
		return null;
	}

	@Override
	public MspResponse checkAction(MspRequest mspRequest) {
		 MspResponse mspResponse = restTemplate
					.postForEntity("http://AM-CONFIG/am-user/mspapplyconfigure/checkAction" ,mspRequest,
							MspResponse.class)
					.getBody();
			if (mspResponse != null) {
				return mspResponse;
			}
			return null;
	}

	/**
	 * 获取所有问题
	 * @return
	 */
	@Override
	public QuestionResponse getAllQuestion(){
		QuestionResponse mspResponse = restTemplate
				.getForEntity("http://AM-CONFIG/am-config/quesiontAndAnswer/findAllQuestion" ,QuestionResponse.class)
				.getBody();
		if (mspResponse != null) {
			return mspResponse;
		}
		return null;
	}

	/**
	 * 获取所有回答
	 * @return
	 */
	@Override
	public AnswerResponse getAllAnswer(){
		AnswerResponse mspResponse = restTemplate
				.getForEntity("http://AM-CONFIG/am-config/quesiontAndAnswer/findAllAnswer" ,AnswerResponse.class)
				.getBody();
		if (mspResponse != null) {
			return mspResponse;
		}
		return null;
	}
}
