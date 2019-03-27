package com.iauto.tools.webguard;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.iauto.tools.webguard.service.MailManager;
import com.iauto.tools.webguard.service.MailManager.MailType;
import com.iauto.tools.webguard.service.ResourceMonitorManager;

@SpringBootApplication
public class WebguardApplication implements ApplicationRunner {
	private static final String PARAM_INIT = "init";
	
	Logger logger = LoggerFactory.getLogger(WebguardApplication.class);
	
	@Value("${resource.config.file}")
	private String resourceConfigFile;
	
	@Value("${resource.sign.file}")
	private String resourceSignFile;
	
	@Autowired
	private ResourceMonitorManager resourceMonitorManager;
	
	@Autowired
	private MailManager mailManager;

	public static void main(String[] args) {

		SpringApplication app = new SpringApplication(WebguardApplication.class);
		app.setBannerMode(Mode.OFF);
		app.run(args);
	}
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (args.containsOption(PARAM_INIT)) {
			logger.info("init mode");
			logger.info("resource config file: {}", resourceConfigFile);
			
			List<String> resourceList = readResources(resourceConfigFile);
			
			for (String file: resourceList) {
				logger.debug("add resource file: {}", file);
				resourceMonitorManager.addResource(file);
			}
			
			logger.info("save resource sign to file: {}", resourceSignFile);;
			resourceMonitorManager.save(resourceSignFile);
		}
		else {
			logger.info("on guard mode");
			logger.debug("load resource sign file from: {}", resourceSignFile);
			resourceMonitorManager.load(resourceSignFile);
			List<String> resourceList = readResources(resourceConfigFile);
			
			// 检查资源是否被修改
			boolean succeeded = resourceMonitorManager.checkResources(resourceList);
			
			if (succeeded) {
				String subject = "safe message";
				String content = "Web Page File Security";
				// 生成邮件内容
				mailManager.sendMail(MailType.SUCCEED, subject, content);
			}
			else {
				String subject = "dangerous message";
				String content = "Page files have been tampered with!";
				mailManager.sendMail(MailType.FAIL, subject, content);
			}
		}
	}

	private List<String> readResources(String resourceConfigFile) throws Exception {
		
		List<String> lines = IOUtils.readLines(new FileInputStream(resourceConfigFile), "utf8");
		List<String> resources = new ArrayList<String>();
		for (String line : lines) {
			File file = new File(line);
			if (file.isFile()) {
				resources.add(line);
			}
			else if (file.isDirectory()) {
				resources.addAll(listDirectoryFiles(line));
			}
		}
		return resources;
	}
	
	
	
	private List<String> listDirectoryFiles(String path) {
		List<String> files = new ArrayList<String>();
		File file = new File(path);
		if (file.isFile()) {
			files.add(path);
		}
		else if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				if (child.isFile()) {
					files.add(child.getAbsolutePath());
				}
				else if (child.isDirectory()) {
					files.addAll(listDirectoryFiles(child.getAbsolutePath()));
				}
			}
		}
		return files;
	}
	
}
