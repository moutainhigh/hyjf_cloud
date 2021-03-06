package com.hyjf.cs.common.util;


import java.io.Serializable;

/**
 *  @desc 封装公共分页组件
 *  @auth zhangyk
 *  @date 2018年6月14日17:41:49
 */
public class Page implements Serializable {

    private static final long serialVersionUID = -4312323165564562319L;

    private int currPage = 1;

    private int pageSize = 10;

    /**
     * 总记录数, -1: 未知
     */
    private int total = -1;

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    /**
     * 每页记录数
     */
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPrevPage() {
        return this.isFirstPage() ? this.getCurrPage() : this.getCurrPage() - 1;
    }

    public int getNextPage() {
        return this.isLastPage() ? this.getCurrPage() : this.getCurrPage() + 1;
    }

    public boolean isFirstPage() {
        return (1 == this.getCurrPage());
    }

    public boolean isLastPage() {
        if(-1 == this.getPageCount()){
            return false;
        }

        return (this.getPageCount() < 1 || this.getPageCount() <= this.getCurrPage());
    }

    /**
     * 页数, 根据total和pageSize计算
     * -1: 未知
     * @return
     */
    public int getPageCount() {
        if(-1 == total){
            return -1;
        }

        if (total < 1) {
            return 0;
        }

        if (pageSize < 1) {
            return 1;
        }

        return (0 == total % pageSize) ? total / pageSize : total / pageSize
                + 1;
    }

    /**
     *
     * mysql offset, 0-based, 根据page和pageSize计算
     *
     * @return
     */
    public int getOffset() {
        if (currPage < 1) {
            return 0;
        }

        return (currPage - 1) * pageSize;
    }

    /**
     * mysql limit, 0-based, 根据page和pageSize计算
     *
     * @return
     */
    public int getLimit() {
        return pageSize;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + currPage;
        result = prime * result + pageSize;
        result = prime * result + total;

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        Page other = (Page) obj;
        if (currPage != other.currPage){
            return false;
        }
        if (pageSize != other.pageSize){
            return false;
        }
        if (total != other.total){
            return false;
        }

        return true;
    }

/*    public String toString() {
        return JSON.toJSONString(this);
    }*/

    /**
     * 初始化默认分页参数
     * @param currentPage
     * @param pageSize
     * @return
     */
    public static Page initPage(int currentPage, int pageSize){
        Page page = new Page();
        page.setCurrPage(currentPage <= 0 ? 1 : currentPage);
        page.setPageSize(pageSize <= 0? 10 : pageSize);
        return page;
    }
}
