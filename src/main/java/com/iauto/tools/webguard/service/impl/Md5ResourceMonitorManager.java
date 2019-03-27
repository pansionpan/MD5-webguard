/**
 *  Copyright(C) 2015 Suntec Software(Shanghai) Co., Ltd.
 *  All Right Reserved.
 */
package com.iauto.tools.webguard.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.iauto.tools.webguard.service.ResourceMonitorManager;

/**
 * Descriptions
 *
 * @version Mar 4, 2019
 * @author SUNTEC
 * @since JDK1.7
 *
 */
@Component
public class Md5ResourceMonitorManager implements ResourceMonitorManager {

	/*
	 * (non-Javadoc)
	 * @see com.iauto.tools.webguard.service.ResourceMonitorManager#addResource(java.lang.String)
	 */
	private Map<String, String> map = new HashMap<String, String>();
	private String WrongFilePath = "WrongFile.conf";

	public Map<String, String> getMap() {
		return map;
	}

	@Override
	public void addResource(String resource) throws Exception {
		if (!StringUtils.isEmpty(resource)) {
			String md5 = makeMD5(resource);
			map.put(resource, md5);
		}
	}

	/*
	 * ï¼ˆnon-Javadocï¼‰
	 * @see com.iauto.tools.webguard.service.ResourceMonitorManager#checkResources(java.util.List)
	 */
	@Override
	public boolean checkResources(List<String> resources) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳

		boolean flag = true;

		if (resources.size() != map.size()) {
			flag = false;
		}
		else {
			for (String s : resources) {
				if (!checkResource(s)) {
					flag = false;
					File f = new File(WrongFilePath);
					if (!f.exists()) {
						f.createNewFile();
					}
					FileWriter fw = new FileWriter(f, true);
					fw.write("DateTime : " + date + "  the filePath : " + s + "\r\n");
					fw.close();
// break;
				}
			}
		}
		return flag;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iauto.tools.webguard.service.ResourceMonitorManager#checkResource(java.lang.String)
	 */
	@Override
	public boolean checkResource(String resource) throws Exception {

		boolean succeeded = true;
		if (!StringUtils.isEmpty(resource)) {
			if (!map.containsKey(resource)) {
				succeeded = false;
			}
			else {
				String resouceMd5 = makeMD5(resource);
				if (resouceMd5 != null) {
					String md5 = map.get(resource);
					if (!resouceMd5.equals(md5)) {
						succeeded = false;
					}
				}
				else {
					succeeded = false;
				}
			}
		}
		return succeeded;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iauto.tools.webguard.service.ResourceMonitorManager#save(java.lang.String)
	 */
	@Override
	public void save(String file) throws Exception {
		if (file == null || file.equals("")) {
			throw new Exception("save file is null or empty");
		}
		else {
			String line = System.getProperty("line.separator");
			StringBuffer str = new StringBuffer();
			FileWriter fw = new FileWriter(file, false);
			Set<Entry<String, String>> set = map.entrySet();
			Iterator<Entry<String, String>> iter = set.iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>)iter.next();
				str.append(entry.getValue() + " : " + entry.getKey()).append(line);
			}
			fw.write(str.toString());
			fw.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.iauto.tools.webguard.service.ResourceMonitorManager#load(java.lang.String)
	 */
	@Override
	public void load(String file) throws Exception {
		if (file == null || file.isEmpty()) {
			throw new Exception("load file is null");
		}
		List<String> lines = IOUtils.readLines(new FileInputStream(file), "utf8");
		for (String line : lines) {
			String s = StringUtils.trimTrailingWhitespace(line);
			if (!StringUtils.isEmpty(s)) {
				String[] fd = new String(s).split(" : ");
				map.put(fd[1], fd[0]);
			}
		}
	}

	/**
	 * save file as MD5 num
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private String makeMD5(String file) throws Exception {

		String md5 = null;
		if (file != null) {
			File file2 = new File(file);
			MessageDigest digest = null;
			FileInputStream in = null;
			byte buffer[] = new byte[1024];
			int len;
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file2);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();

			BigInteger bigInteger = new BigInteger(1, digest.digest());
			md5 = bigInteger.toString(16);
		}
		return md5;
	}
}
