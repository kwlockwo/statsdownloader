package net.dfl.statsdownloder.model;

import java.util.Observable;

import net.dfl.statsdownloder.model.struct.Round;

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

}
