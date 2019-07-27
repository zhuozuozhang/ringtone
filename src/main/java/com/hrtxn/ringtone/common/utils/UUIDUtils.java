package com.hrtxn.ringtone.common.utils;

import java.util.UUID;

/**
 * Author:zcy
 * Date:2019-07-03 9:46
 * Description:UUIDUtils 工具类
 */
public class UUIDUtils {

	 public static void main(String[] args) {
	        System.out.println(get4UUID());
	        System.out.println(get8UUID());
	        System.out.println(get12UUID());
	        System.out.println(get16UUID());
	        System.out.println(get20UUID());
	        System.out.println(get24UUID());
	        System.out.println(get32UUID());
	    }
	    /**
	     * 获得4个长度的十六进制的UUID
	     * @return UUID
	     */
	    public static String get4UUID(){
	        UUID id=UUID.randomUUID();
	        String[] idd=id.toString().split("-");
	        return idd[1];
	        }
	    /**
	     * 获得8个长度的十六进制的UUID
	     * @return UUID
	     */
	    public static String get8UUID(){
	    	UUID id=UUID.randomUUID();
	        String[] idd=id.toString().split("-");
	        return idd[0];
	    }
	    /**
	     * 获得12个长度的十六进制的UUID
	     * @return UUID
	     */
	    public static String get12UUID(){
	    	UUID id=UUID.randomUUID();
	        String[] idd=id.toString().split("-");
	        return idd[0]+idd[1];
	    }
	    /**
	     * 获得16个长度的十六进制的UUID
	     * @return UUID
	     */
	    public static String get16UUID(){

	    	UUID id=UUID.randomUUID();
	        String[] idd=id.toString().split("-");
	        return idd[0]+idd[1]+idd[2];
	    }
	    /**
	     * 获得20个长度的十六进制的UUID
	     * @return UUID
	     */
	    public static String get20UUID(){

	    	UUID id=UUID.randomUUID();
	        String[] idd=id.toString().split("-");
	        return idd[0]+idd[1]+idd[2]+idd[3];
	    }
	    /**
	     * 获得24个长度的十六进制的UUID
	     * @return UUID
	     */
	    public static String get24UUID(){
	    	UUID id=UUID.randomUUID();
	        String[] idd=id.toString().split("-");
	        return idd[0]+idd[1]+idd[4];
	    }
	    /**
	     * 获得32个长度的十六进制的UUID
	     * @return UUID
	     */
	    public static String get32UUID(){
	    	UUID id=UUID.randomUUID();
	        String[] idd=id.toString().split("-");
	        return idd[0]+idd[1]+idd[2]+idd[3]+idd[4];
	    }
}
