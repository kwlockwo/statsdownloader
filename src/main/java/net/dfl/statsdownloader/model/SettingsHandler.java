package net.dfl.statsdownloader.model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.jasypt.util.text.BasicTextEncryptor;

public class SettingsHandler {
	
	public void execute(String proxyYesNo, String proxyHost, String proxyPort, String proxyUser, String proxyPass, String outputDir) throws Exception {
		
		String proxy = "";
		
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
			proxy = "Y";
			System.setProperty("http.proxy", proxy);
			System.setProperty("http.proxyHost.disabled", proxyHost);
			System.setProperty("http.proxyPort.disabled", proxyPort);
			System.setProperty("http.proxyUser.disabled", proxyUser);	
			System.setProperty("http.proxyPassword.disabled", proxyPassEncrypted);	
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
		
		props.store(new FileWriter(propsFilePath), "App Properties File");
	}
}
