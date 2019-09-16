package com.hrtxn.ringtone.common.utils;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * MySQL数据库备份
 * 
 * @author zcy
 */
@Component
public class MySQLDatabaseBackup {

	/** 
     * Java代码实现MySQL数据库导出 
     *  
     * @author zcy 
     * @param hostIP MySQL数据库所在服务器地址IP 
     * @param userName 进入数据库所需要的用户名 
     * @param password 进入数据库所需要的密码 
     * @param savePath 数据库导出文件保存路径 
     * @param fileName 数据库导出文件文件名 
     * @param databaseName 要导出的数据库名 
     * @return 返回true表示导出成功，否则返回false。 
     */  
	public static boolean exportDatabaseTool(String hostIP, String userName,
			String password, String savePath, String fileName,
			String databaseName) throws InterruptedException {
		File saveFile = new File(savePath);
		if (!saveFile.exists()) {// 如果目录不存在
			saveFile.mkdirs();// 创建文件夹 
		}
		if (!savePath.endsWith(File.separator)) {
			savePath = savePath + File.separator;
		}

		PrintWriter printWriter = null;
		BufferedReader bufferedReader = null;
		try {
			printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(savePath + fileName), "utf8"));
			Process process = Runtime.getRuntime().exec("C:\\soft\\mysql-5.7.17-winx64\\bin\\mysqldump -h" + hostIP + " -u" + userName + " -p" + password + " --set-charset=UTF8 " + databaseName);
			InputStreamReader inputStreamReader = new InputStreamReader(
					process.getInputStream(), "utf8");
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				printWriter.println(line);
			}
			printWriter.flush();
			if (process.waitFor() == 0) {//0 表示线程正常终止。
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (printWriter != null) {
					printWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	//定时 每天的23点59分时备份数据库
	@Scheduled(cron = "0 59 23 ? * *")
	public static void sql() {
		Date date = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd SSS");
		try {
			if (exportDatabaseTool("127.0.0.1", "root", "root","C:/ringtoneDatabase", f.format(date) + ".sql", "ringtone")) {
				System.out.println("数据库成功备份！！！");
			} else {
				System.out.println("数据库备份失败！！！");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			if (exportDatabaseTool("127.0.0.1", "root", "root","C:/ringtoneDatabase", "2019-10-14.sql", "ringtone")) {
				System.out.println("数据库成功备份！！！");
			} else {
				System.out.println("数据库备份失败！！！");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
/*
 * "C:\\Program Files\\MySQL\\MySQL Server 5.5\\bin\\mysqldump -h"
 * 
 * java.io.IOException: Cannot run program "mysqldump": CreateProcess error=2,
 * 系统找不到指定的文件。 at java.lang.ProcessBuilder.start(ProcessBuilder.java:1048) at
 * java.lang.Runtime.exec(Runtime.java:620) at
 * java.lang.Runtime.exec(Runtime.java:450) at
 * java.lang.Runtime.exec(Runtime.java:347) at
 * com.hrtxn.hftxapp.utils.MySQLDatabaseBackup
 * .exportDatabaseTool(MySQLDatabaseBackup.java:43) at
 * com.hrtxn.hftxapp.utils.MySQLDatabaseBackup.main(MySQLDatabaseBackup.java:73)
 * Caused by: java.io.IOException: CreateProcess error=2, 系统找不到指定的文件。 at
 * java.lang.ProcessImpl.create(Native Method) at
 * java.lang.ProcessImpl.<init>(ProcessImpl.java:386) at
 * java.lang.ProcessImpl.start(ProcessImpl.java:137) at
 * java.lang.ProcessBuilder.start(ProcessBuilder.java:1029) ... 5 more
 * 数据库备份失败！！！
 */