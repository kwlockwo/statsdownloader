package net.dfl.statsdownloader.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.dfl.statsdownloader.gui.MainWindow;
import net.dfl.statsdownloader.model.ApplicationHandler;
import net.dfl.statsdownloader.model.struct.Round;

public class ApplictionListener implements ActionListener {
	
	private ApplicationHandler handler;
	private MainWindow gui;

	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		if(action.equals("Get Fixture")) {
			try {
				handler.getFixture(gui.getYearInputTxt().getText(), gui.getRoundInputTxt().getText());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if(action.equals("Get Stats")) {
			gui.toggleWorkingCurser(true);
			try {
				handler.getStats();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			gui.toggleWorkingCurser(false);
		} else if(action.equals("OK")) {
			String proxyYesNo = (String)gui.proxyYesNoBox.getSelectedItem();
			String proxyHost = gui.httpProxyHostTxt.getText();
			String proxyPort = gui.httpProxyPortTxt.getText();
			String proxyUser = gui.httpProxyUserTxt.getText();
			String proxyPass = new String(gui.httpProxyPassTxt.getPassword());
			String outputDir = gui.outputDirTxt.getText();
			String debugYesNo = (String)gui.debugYesNoBox.getSelectedItem();
			
			
			try {
				handler.saveSettings(proxyYesNo, proxyHost, proxyPort, proxyUser, proxyPass, outputDir, debugYesNo);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void addHandler(ApplicationHandler handler) {
		this.handler = handler;
	}
	
	public void addGui(MainWindow gui) {
		this.gui = gui;
	}
	
	public void initHandler(Round round) {
		handler.setRound(round);
	}
}
