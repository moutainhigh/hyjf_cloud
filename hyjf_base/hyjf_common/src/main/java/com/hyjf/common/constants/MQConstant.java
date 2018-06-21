package com.hyjf.common.constants;

/**
 * @author xiasq
 * @version MQConstant, v0.1 2018/5/4 13:45
 */
public interface MQConstant {

	/**
	 * 不特别指定的时候使用的默认tag
	 */
	String HYJF_DEFAULT_TAG = "HYJF_DEFAULT_TAG";
	/**
	 * 不特别指定的时候使用的默认key,标识一条消息唯一
	 */
	String HYJF_DEFAULT_KEY = String.valueOf(System.currentTimeMillis());

	/**
	 * 发送短信的 group topic
	 */
	String SMS_CODE_GROUP = "SMS_CODE_GROUP";
	String SMS_CODE_TOPIC = "SMS_CODE_TOPIC";

	/**
	 * 发送邮件的 group topic
	 */
	String MAIL_GROUP = "MAIL_GROUP";
	String MAIL_TOPIC = "MAIL_TOPIC";

	/**
	 * 发送app push group topic
	 */
	String APP_MESSAGE_GROUP = "APP_MESSAGE_GROUP";
	String APP_MESSAGE_TOPIC = "APP_MESSAGE_TOPIC";

	/**
	 * 注册保存资产表account
	 */
	String ACCOUNT_GROUP = "ACCOUNT_GROUP";
	String ACCOUNT_TOPIC = "ACCOUNT_TOPIC";

	/**
	 * 优惠券发放
	 */
	String GRANT_COUPON_GROUP = "COUPON_GROUP";
	String GRANT_COUPON_TOPIC = "COUPON_TOPIC";

	// 注册送188红包
	String REGISTER_COUPON_TOPIC = "REGISTER_COUPON_TOPIC";
	String REGISTER_COUPON_TAG = "REGISTER_COUPON_TAG";
	// 投之家送2张加息券
	String TZJ_REGISTER_INTEREST_TAG = "TZJ_REGISTER_INTEREST_TAG";


	/**
	 * app渠道统计数据
	 */
	String APP_CHANNEL_STATISTICS_DETAIL_GROUP = "APP_CHANNEL_STATISTICS_DETAIL_GROUP";
	String APP_CHANNEL_STATISTICS_DETAIL_TOPIC = "APP_CHANNEL_STATISTICS_DETAIL_TOPIC";
	String APP_CHANNEL_STATISTICS_DETAIL_UPDATE_TAG = "APP_CHANNEL_STATISTICS_DETAIL_UPDATE_TAG";
	String APP_CHANNEL_STATISTICS_DETAIL_SAVE_TAG = "APP_CHANNEL_STATISTICS_DETAIL_SAVE_TAG";

	/**
	 * 资产推送发送mq
	 */
	String ASSET_PUSH_GROUP = "ASSET_PUSH_GROUP";
	String ASSET_PUST_TOPIC = "ASSET_PUSH_TOPIC";

	/**
	 * 自动备案mq
	 */
	String BORROW_RECORD_GROUP = "BORROW_RECORD_GROUP";
	String BORROW_RECORD_TOPIC = "BORROW_RECORD_TOPIC";

	/**
	 * 自动初审mq
	 */
	String BORROW_PREAUDIT_GROUP = "BORROW_PREAUDIT_GROUP";
	String BORROW_PREAUDIT_TOPIC = "BORROW_PREAUDIT_TOPIC";

	/**
	 * 自动关联计划
	 */
	String HYJF_BORROW_ISSUE_GROUP = "HYJF_BORROW_ISSUE_GROUP";
	String HYJF_BORROW_ISSUE_TOPIC = "HYJF_BORROW_ISSUE_TOPIC";

	/**
	 * 网站收支数据统计
	 */
	String ACCOUNT_WEB_LIST_GROUP = "ACCOUNT_WEB_LIST_GROUP";
	String ACCOUNT_WEB_LIST_TOPIC = "ACCOUNT_WEB_LIST_TOPIC";


	/**
	 * 放款，还款请求，放款，还款
	 */
	String BORROW_GROUP = "BORROW_GROUP";

	String BORROW_REPAY_REQUEST_TOPIC = "BORROW_REPAY_REQUEST_TOPIC";
	String BORROW_REPAY_ZT_RESULT_TOPIC = "BORROW_REPAY_ZT_RESULT_TOPIC";
	String BORROW_REPAY_PLAN_RESULT_TOPIC = "BORROW_REPAY_PLAN_RESULT_TOPIC";

	String BORROW_REALTIMELOAN_ZT_REQUEST_TOPIC = "BORROW_REALTIMELOAN_ZT_REQUEST_TOPIC";
	String BORROW_REALTIMELOAN_PLAN_REQUEST_TOPIC = "BORROW_REALTIMELOAN_PLAN_REQUEST_TOPIC";

	/**
	 * 法大大
	 */
	String FDD_GROUP = "FDD_GROUP";
	String BORROW_GENERATE_CONTRACT_TOPIC = "BORROW_GENERATE_CONTRACT_TOPIC";


}