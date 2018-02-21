package net.dfl.statsdownloader;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import org.jasypt.util.text.BasicTextEncryptor;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import net.dfl.statsdownloader.controller.ApplictionListener;
import net.dfl.statsdownloader.gui.MainWindow;
import net.dfl.statsdownloader.model.ApplicationHandler;

public class Run {
	
	Logger logger = (Logger) LoggerFactory.getLogger("statsDownloaderLogger");
	
	public Run() {

		configureLogging();
		
		try {
			logger.info("Stats Downloader Starting");
			loadProperties();
			
			logger.info("Creating ApplicationHandler");
			ApplicationHandler handler = new ApplicationHandler();
			
			logger.info("Creating MainWindow");
			MainWindow mainWindow = new MainWindow();
			
			handler.addObserver(mainWindow);
			
			logger.info("Creating ApplicationListener");
			ApplictionListener listener = new ApplictionListener();
			listener.addHandler(handler);
			listener.addGui(mainWindow);
			listener.initHandler(null);
			
			mainWindow.addFixtureHandler(listener);
			mainWindow.addStatsHandler(listener);
			mainWindow.addSettingsHandler(listener);
			
			logger.info("Stats Downloader Running");
		} catch (Exception ex) {
			logger.error("Error", ex);
		}
	}
	
	private void loadProperties() throws Exception {
		
		logger.info("Loading Properties");
		
		Properties props = new Properties();
		String propFileName = "config.properties";
 
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
 
		if (inputStream != null) {
			props.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
		
		String wdmPath = props.getProperty("wdmpath");
		System.setProperty("wdm.targetPath", wdmPath);
		
		String proxy = props.getProperty("proxy");
		System.setProperty("http.proxy", proxy);
		
		String passwordEncrypted = props.getProperty("proxyPassword");
		String password = "";
		
		if(passwordEncrypted != null && passwordEncrypted.length() > 0) {
			BasicTextEncryptor  encryptor = new BasicTextEncryptor();
			encryptor.setPassword("dfldfl123");
			
			try {
				password = encryptor.decrypt(passwordEncrypted);
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
		System.setProperty("app.debug", props.getProperty("debug"));
		
		if(System.getProperty("app.debug").equals("Y")) {
			logger.debug("Loaded properties: wdmpath={}, proxy={}, proxyHost={}, proxyPort={}, proxyUser={}, outputDir={}, debug={}",
					 	  wdmPath, proxy, props.getProperty("proxyHost"), props.getProperty("proxyPort"), props.getProperty("proxyUser"),
					 	 props.getProperty("outputDir"), props.getProperty("debug"));
		} else {
			logger.setLevel(Level.INFO);
		}
		
		logger.info("Properties Loaded");
	}
	
	private void configureLogging() {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		
		PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
        ple.setContext(lc);
        ple.start();
        
        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setFile("statsdownloader.log");
        fileAppender.setAppend(false);
        fileAppender.setEncoder(ple);
        fileAppender.setContext(lc);
        fileAppender.start();
        
        logger.addAppender(fileAppender);
        logger.setLevel(Level.DEBUG);
        logger.setAdditive(false);	
	}

}
