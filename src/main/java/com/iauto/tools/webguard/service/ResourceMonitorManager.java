/**
 *  Copyright(C) 2015 Suntec Software(Shanghai) Co., Ltd.
 *  All Right Reserved.
 */
package com.iauto.tools.webguard.service;

import java.util.List;

/**
 * Descriptions
 *
 * @version Mar 4, 2019
 * @author SUNTEC
 * @since JDK1.7
 *
 */
public interface ResourceMonitorManager {
	/*
	 * 添加资源
	 */
	void addResource(String resource) throws Exception;
	
	/*
	 * 检查资源
	 */
	boolean checkResource(String resource) throws Exception;
	
	/*
	 * 检查资源
	 */
	boolean checkResources(List<String> resources) throws Exception;
	
	/*
	 * 将当前管理的资源保存到文件
	 */
	void save(String file) throws Exception;
	
	/*
	 * 从文件装载管理资源信息
	 */
	void load(String file) throws Exception;
}
