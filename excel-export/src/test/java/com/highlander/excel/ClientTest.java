package com.highlander.excel;

/**
 * <p> 类说明 </p>
 *
 * @author Alemand
 * @since 2019/11/19
 */
public class ClientTest {

    public static void main(String[] args) {
        String str1 = "java";
        String str2 = "java";
        String str3 = new String("java");
        String str4 = new String("java");
        // true
        System.out.println(str1 == str2);
        // false
        System.out.println(str3 == str4);
        // false
        System.out.println(str1 == str3);
    }
}
