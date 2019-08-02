package com.hrtxn.ringtone.common.utils;

public final class PhoneUtils {
	
	private static String[] telFirst="130,131,132,155,156,166,185,186,145,176".split(",");

    /**
     * 获取随机联通号码
     *
     * @return
     */
    public static String getTel() {
        int index=getNum(0,telFirst.length-1);
        String first=telFirst[index];
        String second=String.valueOf(getNum(1,888)+10000).substring(1);
        String third=String.valueOf(getNum(1,9100)+10000).substring(1);
        return first+second+third;
    }

    /**
     * 获取随机数
     *
     * @param start
     * @param end
     * @return
     */
    private static int getNum(int start,int end) {
        return (int)(Math.random()*(end-start+1)+start);
    }
}
