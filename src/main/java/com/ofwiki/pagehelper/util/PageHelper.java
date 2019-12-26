package com.ofwiki.pagehelper.util;

import java.util.List;

/**
 * @author HuangJS
 * @date 17-11-20 上午11:50.
 */
public class PageHelper {
    private static final ThreadLocal<Page> LOCAL_PAGE = new ThreadLocal();

    public PageHelper() {
    }

    public static <E> Page<E> startPage(int pageNum, int pageSize) {
        Page<E> page = new Page(pageNum, pageSize);
        setLocalPage(page);
        return page;
    }

    public static <E> void setDataIntoPage(List<E> data) {
        Page page = getLocalPage();
        if(page != null) {
            page.setDataList(data);
        }

    }

    public static void setTotalCount(int totalCount) {
        Page page = getLocalPage();
        if(page != null) {
            page.setTotalCount(totalCount);
        }

    }

    protected static void setLocalPage(Page page) {
        LOCAL_PAGE.set(page);
    }

    public static <T> Page<T> getLocalPage() {
        return (Page)LOCAL_PAGE.get();
    }

    public static void clearPage() {
        LOCAL_PAGE.remove();
    }
}
