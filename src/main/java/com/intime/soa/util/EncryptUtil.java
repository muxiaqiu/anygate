package com.intime.soa.util;

import com.intime.soa.framework.exception.ApplicationException;
import com.intime.soa.framework.util.validate.CheckUtil;

/**
 * Created by qmx on 2018/5/16.
 */
public class EncryptUtil {

    /**
     * 对Token进行简单加密（将c变成d，对称翻转）
     *
     * @param token
     * @return
     */
    public static String simpleEncrypt(String token) {
        int total = token.length();
        int half = Math.round(total / 2);
        token = replaceBeginAndEnd(token, half);
        token = subString(token);
        return token;
    }

    public static String simpleDecrypt(String token) {
        String result = subString(token);
        int total = result.length();
        int half = Math.round(total / 2);
        if (total % 2 == 0) {
            half = Math.round(total / 2) - 1;
        }
        token = replaceBeginAndEnd(result, half);
        return token;
    }

    /**
     * 将一个数组以某一元素分界，将这个元素之前的部分与之后的部分互换位置
     * 主要思路:  将之前的部分与之后的部分分别逆序，再将整体逆序即可
     *
     * @param str 要进行操作的字符串
     * @param i   作为分界线的元素的下标
     * @return 转化之后生成的字符串
     */
    public static String replaceBeginAndEnd(String str, int i) {
        if (str == null || i < 0 || str.length() <= i) {
            return str;
        }
        char[] chars = str.toCharArray();
        reverseCharArray(chars, 0, i - 1);
        reverseCharArray(chars, i + 1, chars.length - 1);
        reverseCharArray(chars, 0, chars.length - 1);
        return new String(chars);
    }

    /**
     * 用来翻转一个数组的某一部分
     *
     * @param charArray 要进行操作的数组
     * @param begin     要翻转的部分第一个元素的下标
     * @param end       要翻转的部分最后一个元素的下标
     */
    public static void reverseCharArray(char[] charArray, int begin, int end) {
        char tmp;
        while (begin < end) {
            tmp = charArray[begin];
            charArray[begin] = charArray[end];
            charArray[end] = tmp;
            begin++;
            end--;
        }
    }

    public static String subString(String token) {
        if (!CheckUtil.isEmpty(token) && token.length() > 3) {
            String fir = token.substring(1, 2);
            String last = token.substring(2, 3);
            String tokenfir = token.substring(0,1);
            token = token.substring(3, token.length());
            String result = tokenfir + last + fir + token;
            return result;
        } else {
            throw new ApplicationException("token格式不正确");
        }
    }

    public static void main(String[] args) {
        String token = "ASDSFGH";
        String en = simpleEncrypt(token);
        System.out.println(en);

        String de = simpleDecrypt(en);
        System.out.println(de);


    }
}
