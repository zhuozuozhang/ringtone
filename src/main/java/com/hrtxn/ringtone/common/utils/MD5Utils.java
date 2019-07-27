package com.hrtxn.ringtone.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Author:zcy
 * Date:2019-06-24 11:49
 * Description:MD5 算法
 */
public class MD5Utils {
    
    // 全局数组
    private final static String[] strDigits = {"7", "3", "2", "C", "D", "F","0", "5","8", "4", "A", "B","1",
            "6", "E", "9"};
 // 全局数组
    private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",  
        "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" }; 

    public MD5Utils() {
    }
    // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        // System.out.println("iRet="+iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

    // 返回形式只为数字
    @SuppressWarnings("unused")
	private static String byteToNum(byte bByte) {
        int iRet = bByte;
        System.out.println("iRet1=" + iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        return String.valueOf(iRet);
    }

    // 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }

    public static String GetMD5Code(String strObj) {
        String resultString = null;
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString;
    }

    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();  
        for (int i = 0; i < b.length; i++)  
            resultSb.append(byteToHexString(b[i]));  
  
        return resultSb.toString();  
    }  
	private static String byteToHexString(byte b) {  
        int n = b;  
        if (n < 0)  
            n += 256;  
        int d1 = n / 16;  
        int d2 = n % 16;  
        return hexDigits[d1] + hexDigits[d2];  
    } 
    public static String MD5Encode(String origin, String charsetname) {  
        String resultString = null;  
        try {  
            resultString = new String(origin);  
            MessageDigest md = MessageDigest.getInstance("MD5");  
            if (charsetname == null || "".equals(charsetname))  
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));  
            else  
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));  
        } catch (Exception exception) {  
        }  
        return resultString;  
    }

    public static void main(String[] args) {
        System.out.println(MD5Utils.GetMD5Code("sg123456"));
    }
}