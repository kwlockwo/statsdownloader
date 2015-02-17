package net.dfl.statsdownloder.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.dfl.statsdownloder.gui.MainWindow;
import net.dfl.statsdownloder.model.ApplicationHandler;
import net.dfl.statsdownloder.model.struct.Round;

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
			handler.getStats();
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
