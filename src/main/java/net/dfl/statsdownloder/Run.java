package net.dfl.statsdownloder;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;

import net.dfl.statsdownloder.controller.ApplictionListener;
import net.dfl.statsdownloder.gui.MainWindow;
import net.dfl.statsdownloder.model.ApplicationHandler;

public class Run {
	
	public Run() throws Exception {

		loadProperties();
		
		ApplicationHandler handler = new ApplicationHandler();
		MainWindow mainWindow = new MainWindow();
		
		handler.addObserver(mainWindow);
		
		ApplictionListener listener = new ApplictionListener();
		listener.addHandler(handler);
		listener.addGui(mainWindow);
		listener.initHandler(null);
		
		mainWindow.addFixtureHandler(listener);
		mainWindow.addStatsHandler(listener);
	}
	
	private void loadProperties() throws Exception {
		
		Properties props = new Properties();
		String propFileName = "config.properties";
 
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
 
		if (inputStream != null) {
			props.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
		
		String proxy = props.getProperty("proxy");
		
		System.setProperty("http.proxy", proxy);
		
		String passwordEncrypted = props.getProperty("proxyPassword");
		String password = "";
		
		if(passwordEncrypted != null && passwordEncrypted.length() > 0) {
			BasicTextEncryptor  encryptor = new BasicTextEncryptor();
			encryptor.setPassword("dfldfl123");
			
			try {
				password = encryptor.decrypt(props.getProperty("proxyPassword"));
			} catch (Exception ex) {
				password = "";
			}
		}
				
		if(proxy.equals("Y")) {
			System.setProperty("http.proxyHost", props.getProperty("proxyHost"));
			System.setProperty("http.proxyPort", props.getProperty("proxyPort"));
			System.setProperty("http.proxyUser", props.getProperty("proxyUser"));	
			System.setProperty("http.proxyPassword", password);
		} else {
			System.setProperty("http.proxyHost.disabled", props.getProperty("proxyHost"));
			System.setProperty("http.proxyPort.disabled", props.getProperty("proxyPort"));
			System.setProperty("http.proxyUser.disabled", props.getProperty("proxyUser"));
			System.setProperty("http.proxyPassword.disabled", password);
		}
		
		System.setProperty("app.outputDir", props.getProperty("outputDir"));
	}

}
