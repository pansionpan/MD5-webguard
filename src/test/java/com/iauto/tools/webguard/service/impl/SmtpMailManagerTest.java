/**
 *  Copyright(C) 2015 Suntec Software(Shanghai) Co., Ltd.
 *  All Right Reserved.
 */
package com.iauto.tools.webguard.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.iauto.tools.webguard.service.MailManager.MailType;


/**
 * Descriptions
 * 经测试，有如下结果：
 * 1.类似于邮箱等地址没有验证功能类
 * 2.类似于To和Cc为空，以及内容为空等情况下，应该抛出错误异常。但是测试中并没有抛出异常而是顺利执行
 * 3.类似于发送方以及用户为空情况下，应该抛出错误异常，但是测试中并没有异常抛出而是顺利执行
 * @version 2019年3月21日
 * @author SUNTEC
 * @since JDK1.7
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:test.properties")
@SpringBootTest(classes = {SmtpMailManager.class})
public class SmtpMailManagerTest {
	
	@Autowired
	private SmtpMailManager sm;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() {
		
	}
	
	@After
	public void tearDown() {
		
		sm.setMailFailedCc("2431981052@qq.com");
		sm.setMailFailedTo("541304022@qq.com");
		sm.setMailFrom("test_dotlog@pset.suntec.net");
		sm.setMailSucceededCc("2431981052@qq.com");
		sm.setMailSucceededTo("541304022@qq.com");
		sm.setMailUser("test_dotlog@pset.suntec.net");
		
	}
	
	@AfterClass
	public static void del() {
		//删除自动创建的文件
		new File("SendSucceededTime.conf").delete();
		new File("SendFailedTime.conf").delete();
	}
	
	
	/*
	 * 注解测试
	 */
	@Test
	public void testValueGetSucceededTo() {
		assertEquals("SucceededTo获取错误", "541304022@qq.com", sm.getMailSucceededTo());
	}
	@Test
	public void testValueGetSucceededCc() {
		assertEquals("SucceededCc获取错误", "2431981052@qq.com", sm.getMailSucceededCc());
	}
	@Test
	public void testValueGetFailedTo() {
		assertEquals("FailedTo获取错误", "541304022@qq.com", sm.getMailFailedTo());
	}
	@Test
	public void testValueGetFailedCc() {
		assertEquals("FailedCc获取错误", "2431981052@qq.com", sm.getMailFailedCc());
	}
	@Test
	public void testValueGetSucceededSubject() {
		assertEquals("SucceededSubject获取错误", "File is OK", sm.getMailSucceededSubject());
	}
	@Test
	public void testValueGetFailedSubject() {
		assertEquals("FailedSubject获取错误", "File changed!", sm.getMailFailedSubject());
	}
	@Test
	public void testValueGetFailedInterval() {
		assertEquals("FailedInterval获取错误", 3600000, sm.getMailFailedInterval());
	}
	@Test
	public void testValueGetSucceededInterval() {
		assertEquals("SucceededInterval获取错误", 86400000, sm.getMailSucceededInterval());
	}
	@Test
	public void testValueGetServerHost() {
		assertEquals("ServerHost获取错误", "mail.pset.suntec.net", sm.getMailServerHost());
	}
	@Test
	public void testValueGetServerPort() {
		assertEquals("ServerPort获取错误", 25, sm.getMailServerPort());
	}
	@Test
	public void testValueGetMailFrom() {
		assertEquals("MailFrom获取错误", "test_dotlog@pset.suntec.net", sm.getMailFrom());
	}
	@Test
	public void testValueGetMailUser() {
		assertEquals("MailUser获取错误", "test_dotlog@pset.suntec.net", sm.getMailUser());
	}
	@Test
	public void testValueGetMailPassword() {
		assertEquals("MailPassword获取错误", "111111Aa", sm.getMailPassword());
	}
	@Test
	public void testValueGetRerunTime() {
		assertEquals("RerunTime获取错误", 10, sm.getRerunTime());
	}
	
	//create file
	@Test
	public void testCreateSendFailedTimeConfAndSendSucceededTimeConf() throws IOException {
		FileReader fr = new FileReader("SendSucceededTime.conf");
		BufferedReader br = new BufferedReader(fr);
		assertEquals("SendFailedTimeConfAndSendSucceededTimeConf文件创建失败", "0", br.readLine().toString());
		br.close();
	}
	
	
	
	
	@Test
	public void testSendSucceededMail() throws Exception {
		sm.sendMail(MailType.SUCCEED, "SucceededSubject", "SucceededContent");
	}
	
	@Test
	public void testSendSucceededMailWithSubjectIsNull() throws Exception {
		sm.sendMail(MailType.SUCCEED, null, "SucceededContent");
	}
	
	@Test
	public void testSendSucceededMailWithContentIsNull() throws Exception {
		sm.sendMail(MailType.SUCCEED, "SucceededSubject", null);
	}
	
	@Test
	public void testSendFailedMail() throws Exception {
		sm.sendMail(MailType.FAIL, "SucceededSubject", "SucceededContent");
	}
	
	@Test
	public void testSendFailedMailWithSubjectIsNull() throws Exception {
		sm.sendMail(MailType.FAIL, null, "SucceededContent");
	}
	
	@Test
	public void testSendFailedMailWithContentIsNull() throws Exception {
		sm.sendMail(MailType.FAIL, "SucceededSubject", null);
	}
	
	@Test
	public void testMaillastSendSucceededTimeValue() throws Exception {
		sm.sendMail(MailType.SUCCEED, "SucceededSubject", "SucceededContent");
		FileReader fr = new FileReader("SendSucceededTime.conf");
		BufferedReader br = new BufferedReader(fr);
		assertEquals("MaillastSendSucceededtime读取失败",br.readLine(),sm.getMaillastSendSucceededtime().toString());
		br.close();
	}
	
	@Test
	public void testMaillastSendFailedTimeValue() throws Exception {
		sm.sendMail(MailType.FAIL, "FailSubject", "FailContent");
		FileReader fr = new FileReader("SendFailedTime.conf");
		BufferedReader br = new BufferedReader(fr);
		assertEquals("MaillastSendFailedtime读取失败",br.readLine(),sm.getMaillastSendFailtime().toString());
		br.close();
	}
	
	@Test
	public void testMailFailedInterval() throws Exception{
		sm.sendMail(MailType.FAIL, "FailedSubject", "FailedContent");
		long l = sm.getMaillastSendFailtime();
		sm.sendMail(MailType.FAIL, "FailedSubject2", "FailedContent2");
		long ls = sm.getMaillastSendFailtime();
		assertTrue(l == ls);
	}
	
	@Test
	public void testMailSucceededInterval() throws Exception{
		sm.sendMail(MailType.SUCCEED, "SucceededSubject", "SucceededContent");
		long l = sm.getMaillastSendSucceededtime();
		sm.sendMail(MailType.SUCCEED, "SucceededSubject2", "SucceededContent2");
		long ls = sm.getMaillastSendSucceededtime();
		assertTrue(l == ls);
	}
	
	@Test
	public void testFailedMailFormatCorrect() throws Exception{
		
		sm.setMailFailedTo("1234565");
		sm.setMailFailedCc("123123123");
		sm.sendMail(MailType.FAIL, "FailedSubject", "FailedContent");
		
	}
	
	@Test
	public void testSucceededMailFormatCorrect() throws Exception{
		
		sm.setMailSucceededTo("1234565");
		sm.setMailSucceededCc("123123123");
		sm.sendMail(MailType.SUCCEED, "SucceededSubject", "SucceededContent");
	}
	
	
	@Test
	public void testSendFailedMailWithToIsNull() throws Exception {
		sm.setMailFailedTo("");
		sm.sendMail(MailType.FAIL, "SendFailedMailWithToIsNullSubject", "SendFailedMailWithToIsNullContent");
	}
	
	@Test
	public void testSendFailedMailWithCcIsNull() throws Exception {
		sm.setMailFailedCc("");
		sm.sendMail(MailType.FAIL, "SendFailedMailWithToIsNullSubject", "SendFailedMailWithToIsNullContent");
	}
	
	@Test
	public void testSendSucceededMailWithCcIsNull() throws Exception {
		sm.setMailSucceededCc("");
		sm.sendMail(MailType.SUCCEED, "SendSucceededMailWithCcIsNullSubject", "SendSucceededMailWithCcIsNullContent");
	}
	
	@Test
	public void testSendSucceededMailWithToIsNull() throws Exception {
		sm.setMailSucceededTo("");
		sm.sendMail(MailType.SUCCEED, "SendSucceededMailWithToIsNullSubjcet", "SendSucceededMailWithToIsNullContent");
	}
	
	@Test
	public void testSendSucceededMailWithFromIsNull() throws Exception {
		sm.setMailFrom("");
		sm.sendMail(MailType.SUCCEED, "SendSucceededMailWithFromIsNullSubject", "SendSucceededMailWithFromIsNullContent");
		
	}
	
	@Test
	public void testSendSucceededMailWithUserIsNull() throws Exception {
		sm.setMailUser("");
		sm.sendMail(MailType.SUCCEED, "SendSucceededMailWithUserIsNullSubject", "SendSucceededMailWithUserIsNullContent");
		
	}
	
	@Test
	public void testSendFailedMailWithFromIsNull() throws Exception {
		sm.setMailFrom("");
		sm.sendMail(MailType.FAIL, "SendFailedMailWithFromIsNullSubject", "SendFailedMailWithFromIsNullContent");
		
	}
	
	@Test
	public void testSendFailedMailWithUserIsNull() throws Exception {
		sm.setMailUser("");
		sm.sendMail(MailType.FAIL, "SendFailedMailWithUserIsNullSubject", "SendFailedMailWithUserIsNullContent");
		
	}
}
