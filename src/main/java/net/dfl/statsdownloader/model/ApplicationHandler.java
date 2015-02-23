package net.dfl.statsdownloader.model;

import java.util.Observable;

import net.dfl.statsdownloader.model.struct.Round;

public class ApplicationHandler extends Observable {
	
	private Round round;
	private String year;
	private String roundNo;
	
	public ApplicationHandler() {}
	
	public void setRound(Round round) {
		this.round = round;
		setChanged();
		notifyObservers(round);
	}
	
	public void getFixture(String year, String roundNo) throws Exception {
		
		this.year = year;
		this.roundNo = roundNo;
		
		FixtureHandler handler = new FixtureHandler();
		this.round = handler.execute(year, roundNo);
		setChanged();
		notifyObservers(this.round);
	}
	
	public void getStats() throws Exception {
		StatsHandler handler = new StatsHandler(this.year, this.roundNo);
		handler.execute(this.round);
	}
	
	public void saveSettings(String proxyYesNo, String proxyHost, String proxyPort, String proxyUser, String proxyPass, String outputDir) throws Exception {
		SettingsHandler handler = new SettingsHandler();
		handler.execute(proxyYesNo, proxyHost, proxyPort, proxyUser, proxyPass, outputDir);
		setChanged();
		notifyObservers("SettingsSaved");
	}

}
