/**
 *  Copyright(C) 2015 Suntec Software(Shanghai) Co., Ltd.
 *  All Right Reserved.
 */
package com.iauto.tools.webguard.service;

/**
 * Descriptions
 *
 * @version Mar 4, 2019
 * @author SUNTEC
 * @since JDK1.7
 *
 */
public interface MailManager {
	static enum MailType {
		SUCCEED,
		FAIL
	}
	/*
	 * 发送邮件
	 */
	void sendMail(MailType mailType, String subject, String content) throws Exception;
}
