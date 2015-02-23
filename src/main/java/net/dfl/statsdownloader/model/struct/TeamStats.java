package net.dfl.statsdownloader.model.struct;

import java.util.List;

public class TeamStats {
	
	private String teamId;
	private List<PlayerStats> teamStats;
	
	public String getTeamId() {
		return teamId;
	}
	public List<PlayerStats> getTeamStats() {
		return teamStats;
	}
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}
	public void setTeamStats(List<PlayerStats> teamStats) {
		this.teamStats = teamStats;
	}
}
