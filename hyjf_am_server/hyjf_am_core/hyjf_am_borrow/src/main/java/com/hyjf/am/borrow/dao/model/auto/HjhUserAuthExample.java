package com.hyjf.am.borrow.dao.model.auto;

import java.util.ArrayList;
import java.util.List;

public class HjhUserAuthExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    protected int limitStart = -1;

    protected int limitEnd = -1;

    public HjhUserAuthExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    public void setLimitStart(int limitStart) {
        this.limitStart=limitStart;
    }

    public int getLimitStart() {
        return limitStart;
    }

    public void setLimitEnd(int limitEnd) {
        this.limitEnd=limitEnd;
    }

    public int getLimitEnd() {
        return limitEnd;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNull() {
            addCriterion("user_id is null");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNotNull() {
            addCriterion("user_id is not null");
            return (Criteria) this;
        }

        public Criteria andUserIdEqualTo(Integer value) {
            addCriterion("user_id =", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotEqualTo(Integer value) {
            addCriterion("user_id <>", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThan(Integer value) {
            addCriterion("user_id >", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("user_id >=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThan(Integer value) {
            addCriterion("user_id <", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThanOrEqualTo(Integer value) {
            addCriterion("user_id <=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdIn(List<Integer> values) {
            addCriterion("user_id in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotIn(List<Integer> values) {
            addCriterion("user_id not in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdBetween(Integer value1, Integer value2) {
            addCriterion("user_id between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotBetween(Integer value1, Integer value2) {
            addCriterion("user_id not between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andUserNameIsNull() {
            addCriterion("user_name is null");
            return (Criteria) this;
        }

        public Criteria andUserNameIsNotNull() {
            addCriterion("user_name is not null");
            return (Criteria) this;
        }

        public Criteria andUserNameEqualTo(String value) {
            addCriterion("user_name =", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameNotEqualTo(String value) {
            addCriterion("user_name <>", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameGreaterThan(String value) {
            addCriterion("user_name >", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameGreaterThanOrEqualTo(String value) {
            addCriterion("user_name >=", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameLessThan(String value) {
            addCriterion("user_name <", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameLessThanOrEqualTo(String value) {
            addCriterion("user_name <=", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameLike(String value) {
            addCriterion("user_name like", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameNotLike(String value) {
            addCriterion("user_name not like", value, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameIn(List<String> values) {
            addCriterion("user_name in", values, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameNotIn(List<String> values) {
            addCriterion("user_name not in", values, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameBetween(String value1, String value2) {
            addCriterion("user_name between", value1, value2, "userName");
            return (Criteria) this;
        }

        public Criteria andUserNameNotBetween(String value1, String value2) {
            addCriterion("user_name not between", value1, value2, "userName");
            return (Criteria) this;
        }

        public Criteria andAutoInvesStatusIsNull() {
            addCriterion("auto_inves_status is null");
            return (Criteria) this;
        }

        public Criteria andAutoInvesStatusIsNotNull() {
            addCriterion("auto_inves_status is not null");
            return (Criteria) this;
        }

        public Criteria andAutoInvesStatusEqualTo(Integer value) {
            addCriterion("auto_inves_status =", value, "autoInvesStatus");
            return (Criteria) this;
        }

        public Criteria andAutoInvesStatusNotEqualTo(Integer value) {
            addCriterion("auto_inves_status <>", value, "autoInvesStatus");
            return (Criteria) this;
        }

        public Criteria andAutoInvesStatusGreaterThan(Integer value) {
            addCriterion("auto_inves_status >", value, "autoInvesStatus");
            return (Criteria) this;
        }

        public Criteria andAutoInvesStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("auto_inves_status >=", value, "autoInvesStatus");
            return (Criteria) this;
        }

        public Criteria andAutoInvesStatusLessThan(Integer value) {
            addCriterion("auto_inves_status <", value, "autoInvesStatus");
            return (Criteria) this;
        }

        public Criteria andAutoInvesStatusLessThanOrEqualTo(Integer value) {
            addCriterion("auto_inves_status <=", value, "autoInvesStatus");
            return (Criteria) this;
        }

        public Criteria andAutoInvesStatusIn(List<Integer> values) {
            addCriterion("auto_inves_status in", values, "autoInvesStatus");
            return (Criteria) this;
        }

        public Criteria andAutoInvesStatusNotIn(List<Integer> values) {
            addCriterion("auto_inves_status not in", values, "autoInvesStatus");
            return (Criteria) this;
        }

        public Criteria andAutoInvesStatusBetween(Integer value1, Integer value2) {
            addCriterion("auto_inves_status between", value1, value2, "autoInvesStatus");
            return (Criteria) this;
        }

        public Criteria andAutoInvesStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("auto_inves_status not between", value1, value2, "autoInvesStatus");
            return (Criteria) this;
        }

        public Criteria andAutoCreditStatusIsNull() {
            addCriterion("auto_credit_status is null");
            return (Criteria) this;
        }

        public Criteria andAutoCreditStatusIsNotNull() {
            addCriterion("auto_credit_status is not null");
            return (Criteria) this;
        }

        public Criteria andAutoCreditStatusEqualTo(Integer value) {
            addCriterion("auto_credit_status =", value, "autoCreditStatus");
            return (Criteria) this;
        }

        public Criteria andAutoCreditStatusNotEqualTo(Integer value) {
            addCriterion("auto_credit_status <>", value, "autoCreditStatus");
            return (Criteria) this;
        }

        public Criteria andAutoCreditStatusGreaterThan(Integer value) {
            addCriterion("auto_credit_status >", value, "autoCreditStatus");
            return (Criteria) this;
        }

        public Criteria andAutoCreditStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("auto_credit_status >=", value, "autoCreditStatus");
            return (Criteria) this;
        }

        public Criteria andAutoCreditStatusLessThan(Integer value) {
            addCriterion("auto_credit_status <", value, "autoCreditStatus");
            return (Criteria) this;
        }

        public Criteria andAutoCreditStatusLessThanOrEqualTo(Integer value) {
            addCriterion("auto_credit_status <=", value, "autoCreditStatus");
            return (Criteria) this;
        }

        public Criteria andAutoCreditStatusIn(List<Integer> values) {
            addCriterion("auto_credit_status in", values, "autoCreditStatus");
            return (Criteria) this;
        }

        public Criteria andAutoCreditStatusNotIn(List<Integer> values) {
            addCriterion("auto_credit_status not in", values, "autoCreditStatus");
            return (Criteria) this;
        }

        public Criteria andAutoCreditStatusBetween(Integer value1, Integer value2) {
            addCriterion("auto_credit_status between", value1, value2, "autoCreditStatus");
            return (Criteria) this;
        }

        public Criteria andAutoCreditStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("auto_credit_status not between", value1, value2, "autoCreditStatus");
            return (Criteria) this;
        }

        public Criteria andAutoWithdrawStatusIsNull() {
            addCriterion("auto_withdraw_status is null");
            return (Criteria) this;
        }

        public Criteria andAutoWithdrawStatusIsNotNull() {
            addCriterion("auto_withdraw_status is not null");
            return (Criteria) this;
        }

        public Criteria andAutoWithdrawStatusEqualTo(Integer value) {
            addCriterion("auto_withdraw_status =", value, "autoWithdrawStatus");
            return (Criteria) this;
        }

        public Criteria andAutoWithdrawStatusNotEqualTo(Integer value) {
            addCriterion("auto_withdraw_status <>", value, "autoWithdrawStatus");
            return (Criteria) this;
        }

        public Criteria andAutoWithdrawStatusGreaterThan(Integer value) {
            addCriterion("auto_withdraw_status >", value, "autoWithdrawStatus");
            return (Criteria) this;
        }

        public Criteria andAutoWithdrawStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("auto_withdraw_status >=", value, "autoWithdrawStatus");
            return (Criteria) this;
        }

        public Criteria andAutoWithdrawStatusLessThan(Integer value) {
            addCriterion("auto_withdraw_status <", value, "autoWithdrawStatus");
            return (Criteria) this;
        }

        public Criteria andAutoWithdrawStatusLessThanOrEqualTo(Integer value) {
            addCriterion("auto_withdraw_status <=", value, "autoWithdrawStatus");
            return (Criteria) this;
        }

        public Criteria andAutoWithdrawStatusIn(List<Integer> values) {
            addCriterion("auto_withdraw_status in", values, "autoWithdrawStatus");
            return (Criteria) this;
        }

        public Criteria andAutoWithdrawStatusNotIn(List<Integer> values) {
            addCriterion("auto_withdraw_status not in", values, "autoWithdrawStatus");
            return (Criteria) this;
        }

        public Criteria andAutoWithdrawStatusBetween(Integer value1, Integer value2) {
            addCriterion("auto_withdraw_status between", value1, value2, "autoWithdrawStatus");
            return (Criteria) this;
        }

        public Criteria andAutoWithdrawStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("auto_withdraw_status not between", value1, value2, "autoWithdrawStatus");
            return (Criteria) this;
        }

        public Criteria andAutoConsumeStatusIsNull() {
            addCriterion("auto_consume_status is null");
            return (Criteria) this;
        }

        public Criteria andAutoConsumeStatusIsNotNull() {
            addCriterion("auto_consume_status is not null");
            return (Criteria) this;
        }

        public Criteria andAutoConsumeStatusEqualTo(Integer value) {
            addCriterion("auto_consume_status =", value, "autoConsumeStatus");
            return (Criteria) this;
        }

        public Criteria andAutoConsumeStatusNotEqualTo(Integer value) {
            addCriterion("auto_consume_status <>", value, "autoConsumeStatus");
            return (Criteria) this;
        }

        public Criteria andAutoConsumeStatusGreaterThan(Integer value) {
            addCriterion("auto_consume_status >", value, "autoConsumeStatus");
            return (Criteria) this;
        }

        public Criteria andAutoConsumeStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("auto_consume_status >=", value, "autoConsumeStatus");
            return (Criteria) this;
        }

        public Criteria andAutoConsumeStatusLessThan(Integer value) {
            addCriterion("auto_consume_status <", value, "autoConsumeStatus");
            return (Criteria) this;
        }

        public Criteria andAutoConsumeStatusLessThanOrEqualTo(Integer value) {
            addCriterion("auto_consume_status <=", value, "autoConsumeStatus");
            return (Criteria) this;
        }

        public Criteria andAutoConsumeStatusIn(List<Integer> values) {
            addCriterion("auto_consume_status in", values, "autoConsumeStatus");
            return (Criteria) this;
        }

        public Criteria andAutoConsumeStatusNotIn(List<Integer> values) {
            addCriterion("auto_consume_status not in", values, "autoConsumeStatus");
            return (Criteria) this;
        }

        public Criteria andAutoConsumeStatusBetween(Integer value1, Integer value2) {
            addCriterion("auto_consume_status between", value1, value2, "autoConsumeStatus");
            return (Criteria) this;
        }

        public Criteria andAutoConsumeStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("auto_consume_status not between", value1, value2, "autoConsumeStatus");
            return (Criteria) this;
        }

        public Criteria andAutoCreateTimeIsNull() {
            addCriterion("auto_create_time is null");
            return (Criteria) this;
        }

        public Criteria andAutoCreateTimeIsNotNull() {
            addCriterion("auto_create_time is not null");
            return (Criteria) this;
        }

        public Criteria andAutoCreateTimeEqualTo(Integer value) {
            addCriterion("auto_create_time =", value, "autoCreateTime");
            return (Criteria) this;
        }

        public Criteria andAutoCreateTimeNotEqualTo(Integer value) {
            addCriterion("auto_create_time <>", value, "autoCreateTime");
            return (Criteria) this;
        }

        public Criteria andAutoCreateTimeGreaterThan(Integer value) {
            addCriterion("auto_create_time >", value, "autoCreateTime");
            return (Criteria) this;
        }

        public Criteria andAutoCreateTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("auto_create_time >=", value, "autoCreateTime");
            return (Criteria) this;
        }

        public Criteria andAutoCreateTimeLessThan(Integer value) {
            addCriterion("auto_create_time <", value, "autoCreateTime");
            return (Criteria) this;
        }

        public Criteria andAutoCreateTimeLessThanOrEqualTo(Integer value) {
            addCriterion("auto_create_time <=", value, "autoCreateTime");
            return (Criteria) this;
        }

        public Criteria andAutoCreateTimeIn(List<Integer> values) {
            addCriterion("auto_create_time in", values, "autoCreateTime");
            return (Criteria) this;
        }

        public Criteria andAutoCreateTimeNotIn(List<Integer> values) {
            addCriterion("auto_create_time not in", values, "autoCreateTime");
            return (Criteria) this;
        }

        public Criteria andAutoCreateTimeBetween(Integer value1, Integer value2) {
            addCriterion("auto_create_time between", value1, value2, "autoCreateTime");
            return (Criteria) this;
        }

        public Criteria andAutoCreateTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("auto_create_time not between", value1, value2, "autoCreateTime");
            return (Criteria) this;
        }

        public Criteria andAutoOrderIdIsNull() {
            addCriterion("auto_order_id is null");
            return (Criteria) this;
        }

        public Criteria andAutoOrderIdIsNotNull() {
            addCriterion("auto_order_id is not null");
            return (Criteria) this;
        }

        public Criteria andAutoOrderIdEqualTo(String value) {
            addCriterion("auto_order_id =", value, "autoOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoOrderIdNotEqualTo(String value) {
            addCriterion("auto_order_id <>", value, "autoOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoOrderIdGreaterThan(String value) {
            addCriterion("auto_order_id >", value, "autoOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoOrderIdGreaterThanOrEqualTo(String value) {
            addCriterion("auto_order_id >=", value, "autoOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoOrderIdLessThan(String value) {
            addCriterion("auto_order_id <", value, "autoOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoOrderIdLessThanOrEqualTo(String value) {
            addCriterion("auto_order_id <=", value, "autoOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoOrderIdLike(String value) {
            addCriterion("auto_order_id like", value, "autoOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoOrderIdNotLike(String value) {
            addCriterion("auto_order_id not like", value, "autoOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoOrderIdIn(List<String> values) {
            addCriterion("auto_order_id in", values, "autoOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoOrderIdNotIn(List<String> values) {
            addCriterion("auto_order_id not in", values, "autoOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoOrderIdBetween(String value1, String value2) {
            addCriterion("auto_order_id between", value1, value2, "autoOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoOrderIdNotBetween(String value1, String value2) {
            addCriterion("auto_order_id not between", value1, value2, "autoOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoCreditOrderIdIsNull() {
            addCriterion("auto_credit_order_id is null");
            return (Criteria) this;
        }

        public Criteria andAutoCreditOrderIdIsNotNull() {
            addCriterion("auto_credit_order_id is not null");
            return (Criteria) this;
        }

        public Criteria andAutoCreditOrderIdEqualTo(String value) {
            addCriterion("auto_credit_order_id =", value, "autoCreditOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoCreditOrderIdNotEqualTo(String value) {
            addCriterion("auto_credit_order_id <>", value, "autoCreditOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoCreditOrderIdGreaterThan(String value) {
            addCriterion("auto_credit_order_id >", value, "autoCreditOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoCreditOrderIdGreaterThanOrEqualTo(String value) {
            addCriterion("auto_credit_order_id >=", value, "autoCreditOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoCreditOrderIdLessThan(String value) {
            addCriterion("auto_credit_order_id <", value, "autoCreditOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoCreditOrderIdLessThanOrEqualTo(String value) {
            addCriterion("auto_credit_order_id <=", value, "autoCreditOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoCreditOrderIdLike(String value) {
            addCriterion("auto_credit_order_id like", value, "autoCreditOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoCreditOrderIdNotLike(String value) {
            addCriterion("auto_credit_order_id not like", value, "autoCreditOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoCreditOrderIdIn(List<String> values) {
            addCriterion("auto_credit_order_id in", values, "autoCreditOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoCreditOrderIdNotIn(List<String> values) {
            addCriterion("auto_credit_order_id not in", values, "autoCreditOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoCreditOrderIdBetween(String value1, String value2) {
            addCriterion("auto_credit_order_id between", value1, value2, "autoCreditOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoCreditOrderIdNotBetween(String value1, String value2) {
            addCriterion("auto_credit_order_id not between", value1, value2, "autoCreditOrderId");
            return (Criteria) this;
        }

        public Criteria andAutoCreditTimeIsNull() {
            addCriterion("auto_credit_time is null");
            return (Criteria) this;
        }

        public Criteria andAutoCreditTimeIsNotNull() {
            addCriterion("auto_credit_time is not null");
            return (Criteria) this;
        }

        public Criteria andAutoCreditTimeEqualTo(Integer value) {
            addCriterion("auto_credit_time =", value, "autoCreditTime");
            return (Criteria) this;
        }

        public Criteria andAutoCreditTimeNotEqualTo(Integer value) {
            addCriterion("auto_credit_time <>", value, "autoCreditTime");
            return (Criteria) this;
        }

        public Criteria andAutoCreditTimeGreaterThan(Integer value) {
            addCriterion("auto_credit_time >", value, "autoCreditTime");
            return (Criteria) this;
        }

        public Criteria andAutoCreditTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("auto_credit_time >=", value, "autoCreditTime");
            return (Criteria) this;
        }

        public Criteria andAutoCreditTimeLessThan(Integer value) {
            addCriterion("auto_credit_time <", value, "autoCreditTime");
            return (Criteria) this;
        }

        public Criteria andAutoCreditTimeLessThanOrEqualTo(Integer value) {
            addCriterion("auto_credit_time <=", value, "autoCreditTime");
            return (Criteria) this;
        }

        public Criteria andAutoCreditTimeIn(List<Integer> values) {
            addCriterion("auto_credit_time in", values, "autoCreditTime");
            return (Criteria) this;
        }

        public Criteria andAutoCreditTimeNotIn(List<Integer> values) {
            addCriterion("auto_credit_time not in", values, "autoCreditTime");
            return (Criteria) this;
        }

        public Criteria andAutoCreditTimeBetween(Integer value1, Integer value2) {
            addCriterion("auto_credit_time between", value1, value2, "autoCreditTime");
            return (Criteria) this;
        }

        public Criteria andAutoCreditTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("auto_credit_time not between", value1, value2, "autoCreditTime");
            return (Criteria) this;
        }

        public Criteria andAutoBidTimeIsNull() {
            addCriterion("auto_bid_time is null");
            return (Criteria) this;
        }

        public Criteria andAutoBidTimeIsNotNull() {
            addCriterion("auto_bid_time is not null");
            return (Criteria) this;
        }

        public Criteria andAutoBidTimeEqualTo(Integer value) {
            addCriterion("auto_bid_time =", value, "autoBidTime");
            return (Criteria) this;
        }

        public Criteria andAutoBidTimeNotEqualTo(Integer value) {
            addCriterion("auto_bid_time <>", value, "autoBidTime");
            return (Criteria) this;
        }

        public Criteria andAutoBidTimeGreaterThan(Integer value) {
            addCriterion("auto_bid_time >", value, "autoBidTime");
            return (Criteria) this;
        }

        public Criteria andAutoBidTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("auto_bid_time >=", value, "autoBidTime");
            return (Criteria) this;
        }

        public Criteria andAutoBidTimeLessThan(Integer value) {
            addCriterion("auto_bid_time <", value, "autoBidTime");
            return (Criteria) this;
        }

        public Criteria andAutoBidTimeLessThanOrEqualTo(Integer value) {
            addCriterion("auto_bid_time <=", value, "autoBidTime");
            return (Criteria) this;
        }

        public Criteria andAutoBidTimeIn(List<Integer> values) {
            addCriterion("auto_bid_time in", values, "autoBidTime");
            return (Criteria) this;
        }

        public Criteria andAutoBidTimeNotIn(List<Integer> values) {
            addCriterion("auto_bid_time not in", values, "autoBidTime");
            return (Criteria) this;
        }

        public Criteria andAutoBidTimeBetween(Integer value1, Integer value2) {
            addCriterion("auto_bid_time between", value1, value2, "autoBidTime");
            return (Criteria) this;
        }

        public Criteria andAutoBidTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("auto_bid_time not between", value1, value2, "autoBidTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Integer value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Integer value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Integer value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Integer value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Integer value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Integer> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Integer> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Integer value1, Integer value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateUserIsNull() {
            addCriterion("create_user is null");
            return (Criteria) this;
        }

        public Criteria andCreateUserIsNotNull() {
            addCriterion("create_user is not null");
            return (Criteria) this;
        }

        public Criteria andCreateUserEqualTo(Integer value) {
            addCriterion("create_user =", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotEqualTo(Integer value) {
            addCriterion("create_user <>", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserGreaterThan(Integer value) {
            addCriterion("create_user >", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserGreaterThanOrEqualTo(Integer value) {
            addCriterion("create_user >=", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLessThan(Integer value) {
            addCriterion("create_user <", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLessThanOrEqualTo(Integer value) {
            addCriterion("create_user <=", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserIn(List<Integer> values) {
            addCriterion("create_user in", values, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotIn(List<Integer> values) {
            addCriterion("create_user not in", values, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserBetween(Integer value1, Integer value2) {
            addCriterion("create_user between", value1, value2, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotBetween(Integer value1, Integer value2) {
            addCriterion("create_user not between", value1, value2, "createUser");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Integer value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Integer value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Integer value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Integer value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Integer value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Integer> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Integer> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Integer value1, Integer value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateUserIsNull() {
            addCriterion("update_user is null");
            return (Criteria) this;
        }

        public Criteria andUpdateUserIsNotNull() {
            addCriterion("update_user is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateUserEqualTo(Integer value) {
            addCriterion("update_user =", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotEqualTo(Integer value) {
            addCriterion("update_user <>", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserGreaterThan(Integer value) {
            addCriterion("update_user >", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserGreaterThanOrEqualTo(Integer value) {
            addCriterion("update_user >=", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserLessThan(Integer value) {
            addCriterion("update_user <", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserLessThanOrEqualTo(Integer value) {
            addCriterion("update_user <=", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserIn(List<Integer> values) {
            addCriterion("update_user in", values, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotIn(List<Integer> values) {
            addCriterion("update_user not in", values, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserBetween(Integer value1, Integer value2) {
            addCriterion("update_user between", value1, value2, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotBetween(Integer value1, Integer value2) {
            addCriterion("update_user not between", value1, value2, "updateUser");
            return (Criteria) this;
        }

        public Criteria andDelFlgIsNull() {
            addCriterion("del_flg is null");
            return (Criteria) this;
        }

        public Criteria andDelFlgIsNotNull() {
            addCriterion("del_flg is not null");
            return (Criteria) this;
        }

        public Criteria andDelFlgEqualTo(Integer value) {
            addCriterion("del_flg =", value, "delFlg");
            return (Criteria) this;
        }

        public Criteria andDelFlgNotEqualTo(Integer value) {
            addCriterion("del_flg <>", value, "delFlg");
            return (Criteria) this;
        }

        public Criteria andDelFlgGreaterThan(Integer value) {
            addCriterion("del_flg >", value, "delFlg");
            return (Criteria) this;
        }

        public Criteria andDelFlgGreaterThanOrEqualTo(Integer value) {
            addCriterion("del_flg >=", value, "delFlg");
            return (Criteria) this;
        }

        public Criteria andDelFlgLessThan(Integer value) {
            addCriterion("del_flg <", value, "delFlg");
            return (Criteria) this;
        }

        public Criteria andDelFlgLessThanOrEqualTo(Integer value) {
            addCriterion("del_flg <=", value, "delFlg");
            return (Criteria) this;
        }

        public Criteria andDelFlgIn(List<Integer> values) {
            addCriterion("del_flg in", values, "delFlg");
            return (Criteria) this;
        }

        public Criteria andDelFlgNotIn(List<Integer> values) {
            addCriterion("del_flg not in", values, "delFlg");
            return (Criteria) this;
        }

        public Criteria andDelFlgBetween(Integer value1, Integer value2) {
            addCriterion("del_flg between", value1, value2, "delFlg");
            return (Criteria) this;
        }

        public Criteria andDelFlgNotBetween(Integer value1, Integer value2) {
            addCriterion("del_flg not between", value1, value2, "delFlg");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}