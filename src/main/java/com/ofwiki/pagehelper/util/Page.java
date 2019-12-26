package com.ofwiki.pagehelper.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HuangJS
 * @date 17-11-20 上午11:49.
 */
public class Page<E> {
    private int pageNum = 1;
    private int pageSize = 10;
    private int startRow;
    private int totalCount;
    private int totalPage;
    private List<E> dataList;

    public Page() {
    }

    public Page(int pageNum, int pageSize) {
        if(pageNum >= 1) {
            this.pageNum = pageNum;
        }

        if(pageSize >= 1) {
            this.pageSize = pageSize;
        }

        this.calculateStartAndEndRow();
    }

    private void calculateStartAndEndRow() {
        this.startRow = (this.pageNum - 1) * this.pageSize;
    }

    public int getStartRow() {
        return this.startRow;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        if(totalCount % this.pageSize == 0) {
            this.totalPage = totalCount / this.pageSize;
        } else {
            this.totalPage = totalCount / this.pageSize + 1;
        }

    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public List<E> getDataList() {
        return this.dataList==null?new ArrayList():new ArrayList(this.dataList);
    }

    public void setDataList(List<E> dataList) {
        this.dataList = dataList;
    }

    public int getPageNum() {
        return this.pageNum;
    }
}
