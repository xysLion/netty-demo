package com.ancun.common.utils;

import com.ancun.common.exception.EduException;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;

import javax.annotation.Nullable;

import static com.ancun.common.constant.Constants.COMMA;
import static com.ancun.common.constant.ResponseConst.PARAMS_PHONENO_IS_ERROR;

/**
 * 参数验证工具类。
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class ParamValid {

    /**
     * 验证参数是否为空
     *
     * @param param
     * @param code
     */
    public static void checkParam(Object param, int code){
        if (param == null || param.toString().length() <= 0) {
            throw new EduException(code);
        }
    }

    /**
     * 校验号码列表
     *
     * @param phoneStr   号码列表(string)
     */
    public static void checkPhone(final String phoneStr, MobileUtil mobileUtil) {
        // 以逗号分割phoneStr字符串
        Iterable<String> emailToTempIt = Splitter.on(COMMA)
                .omitEmptyStrings().trimResults().split(phoneStr);

        // 格式不正确号码列表
        Iterable<String> invalidTos = FluentIterable.from(emailToTempIt).filter(isNotMobile(mobileUtil));
        // 如果有错误列表则抛出异常
        if (invalidTos.iterator().hasNext()) {
            throw new EduException(PARAMS_PHONENO_IS_ERROR, new Object[] {invalidTos.toString()});
        }
    }

    /**
     * 不是手机号码
     *
     * @return 判读表达式
     * @param mobileUtil
     */
    private static Predicate<String> isNotMobile(final MobileUtil mobileUtil) {
        return new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String input) {
                return !mobileUtil.isMobile(input);
            }
        };
    }

    public static void main(String[] args) {
        try {
            String phone = "18694586638,18694";
//            ParamValid.checkPhone(phone);
        } catch (EduException e) {
            System.out.println(e.getCode());
        }
    }
}
