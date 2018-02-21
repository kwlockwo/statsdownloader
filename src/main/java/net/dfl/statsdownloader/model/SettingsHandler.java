package net.dfl.statsdownloader.model;

import java.io.FileWriter;
import java.util.Properties;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class SettingsHandler {
	Logger logger = (Logger) LoggerFactory.getLogger("statsDownloaderLogger");
	
	public void execute(String proxyYesNo, String proxyHost, String proxyPort, String proxyUser, String proxyPass, String outputDir, String debugYesNo) throws Exception {
		
		String proxy = "";
		String debug = "";
		
		String proxyPassEncrypted = "";
				
		if(proxyPass != null && proxyPass.length() > 0) {
			BasicTextEncryptor  encryptor = new BasicTextEncryptor();
			encryptor.setPassword("dfldfl123");
			
			try {
				proxyPassEncrypted = encryptor.encrypt(proxyPass);
			} catch (Exception ex) {
				proxyPassEncrypted = "";
			}
		}
		
		if(proxyYesNo.equals("Yes")) {
			proxy = "Y";
			System.setProperty("http.proxy", proxy);
			System.setProperty("http.proxyHost", proxyHost);
			System.setProperty("http.proxyPort", proxyPort);
			System.setProperty("http.proxyUser", proxyUser);	
			System.setProperty("http.proxyPassword", proxyPassEncrypted);			
		} else {
			proxy = "N";
			System.setProperty("http.proxy", proxy);
			System.setProperty("http.proxyHost.disabled", proxyHost);
			System.setProperty("http.proxyPort.disabled", proxyPort);
			System.setProperty("http.proxyUser.disabled", proxyUser);	
			System.setProperty("http.proxyPassword.disabled", proxyPassEncrypted);	
		}
		
		System.setProperty("app.outputDir", outputDir);
		
		if(debugYesNo.equals("Yes")) {
			debug = "Y";
			System.setProperty("app.debug", debug);
			logger.setLevel(Level.DEBUG);
		} else {
			debug = "N";
			System.setProperty("app.debug", debug);
			logger.setLevel(Level.INFO);
		}
		
		String propFileName = "config.properties";
		String propsFilePath = getClass().getClassLoader().getResource(propFileName).getPath();
		
		Properties props = new Properties();
		props.setProperty("proxy", proxy);
		props.setProperty("proxyHost", proxyHost);
		props.setProperty("proxyPort", proxyPort);
		props.setProperty("proxyUser", proxyUser);
		props.setProperty("proxyPassword", proxyPassEncrypted);
		props.setProperty("outputDir", outputDir);
		props.setProperty("debug", debug);
		props.setProperty("wdmpath", System.getProperty("wdm.targetPath"));
		
		props.store(new FileWriter(propsFilePath), "App Properties File");
	}
}
