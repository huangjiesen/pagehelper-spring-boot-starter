package com.ofwiki.pagehelper.dialect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author HuangJS
 * @date 17-11-20 上午11:45.
 */
public class PageDialectHelper {
    public PageDialectHelper() {
    }

    public static String getCountString(String sql) {
        String querySelect = getLineSql(sql);
        int orderIndex = getLastOrderInsertPoint(querySelect);
        int fromIndex = getAfterFormInsertPoint(querySelect);
        String select = querySelect.substring(0, fromIndex);
        return select.toLowerCase().indexOf("select distinct") == -1 && querySelect.toLowerCase().indexOf("group by") == -1?"select count(1) count " + querySelect.substring(fromIndex, orderIndex):"select count(1) count from (" + querySelect.substring(0, orderIndex) + " ) t";
    }

    private static int getLastOrderInsertPoint(String querySelect) {
        int orderIndex = querySelect.toLowerCase().lastIndexOf("order by");
        if(orderIndex == -1) {
            orderIndex = querySelect.length();
        }

        if(!isBracketCanPartnership(querySelect.substring(orderIndex, querySelect.length()))) {
            throw new RuntimeException("Sql语句'('与')'数量不匹配");
        } else {
            return orderIndex;
        }
    }

    public static String getLineSql(String sql) {
        return sql.replaceAll("[\r\n]", " ").replaceAll("\\s{2,}", " ");
    }

    private static int getAfterFormInsertPoint(String querySelect) {
        String regex = "\\s+FROM\\s+";
        Pattern pattern = Pattern.compile(regex, 2);
        Matcher matcher = pattern.matcher(querySelect);

        int fromStartIndex;
        String text;
        do {
            if(!matcher.find()) {
                return 0;
            }

            fromStartIndex = matcher.start(0);
            text = querySelect.substring(0, fromStartIndex);
        } while(!isBracketCanPartnership(text));

        return fromStartIndex;
    }

    private static boolean isBracketCanPartnership(String text) {
        return text != null && getIndexOfCount(text, '(') == getIndexOfCount(text, ')');
    }

    private static int getIndexOfCount(String text, char ch) {
        int count = 0;

        for(int i = 0; i < text.length(); ++i) {
            count = text.charAt(i) == ch?count + 1:count;
        }

        return count;
    }
}
