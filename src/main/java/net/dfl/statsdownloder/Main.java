package net.dfl.statsdownloder;

import net.dfl.statsdownloder.controller.ApplictionListener;
import net.dfl.statsdownloder.gui.MainWindow;
import net.dfl.statsdownloder.model.ApplicationHandler;

public class Main {

	public static void main(String[] args) {
		
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

}
