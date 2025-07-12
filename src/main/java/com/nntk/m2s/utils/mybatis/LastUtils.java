package com.nntk.m2s.utils.mybatis;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class LastUtils {


    public static String orderByField(String fieldName, List<Long> values) {

        return "order by field(" + fieldName + "," +
                StringUtils.join(values, ",") +
                ")";
    }


    public static String orderByIntField(String fieldName, List<Integer> values) {

        return "order by field(" + fieldName + "," +
                StringUtils.join(values, ",") +
                ")";
    }

    public static String ifNullNotEq(String fieldName, Integer value) {

        return " AND IFNULL(" + fieldName + ",'') <> " + value;
    }


    public static String orderByDesc(String fieldName) {
        return " ORDER BY " + fieldName + " DESC";

    }

}
