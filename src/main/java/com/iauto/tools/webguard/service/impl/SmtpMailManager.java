package com.iauto.tools.webguard.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.iauto.tools.webguard.WebguardApplication;
import com.iauto.tools.webguard.service.MailManager;

@SpringBootApplication
public class SmtpMailManager implements  MailManager{
	
	Logger logger = LoggerFactory.getLogger(WebguardApplication.class);

	@Value("${mail.succeeded.to}")
	private String mailSucceededTo;

	@Value("${mail.succeeded.cc}")
	private String mailSucceededCc;
	
	@Value("${mail.failed.to}")
	private String mailFailedTo;
	
	@Value("${mail.failed.cc}")
	private String mailFailedCc;
	
	@Value("${mail.succeeded.subject}")
	private String mailSucceededSubject;
	
	@Value("${mail.failed.subject}")
	private String mailFailedSubject;

	@Value("${mail.failed.interval}")
	private int mailFailedInterval;

	@Value("${mail.succeeded.interval}")
	private int mailSucceededInterval;
	
	@Value("${mail.server.host}")
	private String mailServerHost;
	
	@Value("${mail.server.port}")
	private int mailServerPort;

	@Value("${mail.from}")
	private String mailFrom;

	@Value("${mail.user}")
	private String mailUser;

	@Value("${mail.password}")
	private String mailPassword;
	
	@Value("${rerun.time}")
	private int rerunTime;
	
	private Long MaillastSendFailtime = 0L;

	private Long MaillastSendSucceededtime = 0L;

	private BufferedReader brSucceed;

	private BufferedReader brFail;
	
	private String fSucceed = "SendSucceededTime.conf";
	private String fFail ="SendFailedTime.conf";
	
	
	//get()
	public String getMailSucceededCc() {
		return mailSucceededCc;
	}

	public String getMailFailedTo() {
		return mailFailedTo;
	}

	public String getMailFailedCc() {
		return mailFailedCc;
	}

	public String getMailSucceededSubject() {
		return mailSucceededSubject;
	}

	public String getMailFailedSubject() {
		return mailFailedSubject;
	}

	public int getMailFailedInterval() {
		return mailFailedInterval;
	}

	public int getMailSucceededInterval() {
		return mailSucceededInterval;
	}

	public String getMailServerHost() {
		return mailServerHost;
	}

	public int getMailServerPort() {
		return mailServerPort;
	}

	public String getMailFrom() {
		return mailFrom;
	}

	public String getMailUser() {
		return mailUser;
	}

	public String getMailPassword() {
		return mailPassword;
	}

	public int getRerunTime() {
		return rerunTime;
	}

	public String getMailSucceededTo() {
		return mailSucceededTo;
	}
	
	public Long getMaillastSendFailtime() {
		return MaillastSendFailtime;
	}

	public Long getMaillastSendSucceededtime() {
		return MaillastSendSucceededtime;
	}
	
	public void setMailSucceededTo(String mailSucceededTo) {
		this.mailSucceededTo = mailSucceededTo;
	}

	public void setMailSucceededCc(String mailSucceededCc) {
		this.mailSucceededCc = mailSucceededCc;
	}

	public void setMailFailedTo(String mailFailedTo) {
		this.mailFailedTo = mailFailedTo;
	}

	public void setMailFailedCc(String mailFailedCc) {
		this.mailFailedCc = mailFailedCc;
	}
	
	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}
	

	public void setMailUser(String mailUser) {
		this.mailUser = mailUser;
	}

	
	private void writeFlie(Long sendtime, String path) throws IOException {
		File f = new File(path);
		FileWriter fw = new FileWriter(f);
		fw.write(sendtime.toString());
		fw.close();
	}
	
	public SmtpMailManager() throws IOException{
		// load last send time
		File fs = new File(fSucceed);
		if(!fs.exists()) {
			fs.createNewFile();
			writeFlie(0L, fSucceed);
		}else {
			FileReader frSucceed = new FileReader(fSucceed);
			brSucceed = new BufferedReader(frSucceed);
			MaillastSendSucceededtime = Long.parseLong(brSucceed.readLine());
		}
		
		File ff = new File(fFail);
		if(!ff.exists()) {
			ff.createNewFile();
			writeFlie(0L, fFail);
		}else {
			FileReader frFail = new FileReader(fFail);
			brFail = new BufferedReader(frFail);
			MaillastSendFailtime = Long.parseLong(brFail.readLine());
		}
		
	}
	
	@Override
	public void sendMail(MailType mailType, String subject, String content) throws Exception {
		
		String to = null;
		String cc = null;
		boolean needSendMail = true;
		Date dt = new Date();
		Calendar c = Calendar.getInstance(); 
		int hour = c.get(Calendar.HOUR_OF_DAY); 
		int minute = c.get(Calendar.MINUTE); 
		int timeinterval = Math.abs(hour * 60 + minute - 480); 
		
		if (mailType == MailType.SUCCEED) {
			needSendMail =((dt.getTime() - MaillastSendSucceededtime) >= mailSucceededInterval && timeinterval <= (rerunTime / 2));
			to = mailSucceededTo;
			cc = mailSucceededCc;
			logger.info("succeed");
		}
		else if (mailType == MailType.FAIL) {
			needSendMail = (dt.getTime() - MaillastSendFailtime) >= mailFailedInterval;
			to = mailFailedTo;
			cc = mailFailedCc;
			logger.info("fail");
		}

		if (needSendMail) {
			Properties props = new Properties();
			
			props.setProperty("mail.smtp.auth", "true");
			props.setProperty("mail.transport.protocol", "smtp");

			Session session = Session.getInstance(props);
			// session.setDebug(true);
			Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress(mailFrom));
			message.setSubject(subject);
			message.setText(content);
			
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));

			Transport transport = session.getTransport();

			transport.connect(mailServerHost, mailServerPort, mailUser, mailPassword);

			transport.sendMessage(message,new Address[]{new InternetAddress(to)});

			transport.close();

			if (mailType == MailType.SUCCEED) {
				MaillastSendSucceededtime = dt.getTime();
				// Save last time
				writeFlie(MaillastSendSucceededtime, fSucceed);
			}
			else if (mailType == MailType.FAIL) {
				MaillastSendFailtime = dt.getTime();
				// Save last time
				writeFlie(MaillastSendFailtime, fFail);
			}
		}
		
	}
}
