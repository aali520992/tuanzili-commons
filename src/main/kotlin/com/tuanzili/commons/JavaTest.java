package com.tuanzili.commons;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author Panda
 */
public class JavaTest {


    public static void main(String[] args) {
        // 01100
        int a = 12;
        // 11000
        int b = 24;
        // ^符号是按位异或，对应位数相同为0，不同为1
        // 01100(a) ^ 11000(b) = 10100(10进制为20)赋值给a
        a = a ^ b;
        // 10100(a) ^ 11000(b) = 01100(10进制为12)赋值给b
        b = a ^ b;
        // 10100(a) ^ 01100(b) = 11000(10进制为24)赋值给a
        a = a ^ b;
        // 操作完成后，a和b的变量就交换了
        System.out.println("a:" + a);
        System.out.println("b:" + b);
    }

}
