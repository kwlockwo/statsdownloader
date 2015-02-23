package net.dfl.statsdownloader.model;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import au.com.bytecode.opencsv.CSVWriter;
import net.dfl.statsdownloader.model.struct.Fixture;
import net.dfl.statsdownloader.model.struct.PlayerStats;
import net.dfl.statsdownloader.model.struct.Round;
import net.dfl.statsdownloader.model.struct.RoundStats;
import net.dfl.statsdownloader.model.struct.TeamStats;

public class StatsHandler {
	
	private String year;
	private String round;
	
	List<PlayerStats> leftoverPlayers;
	
	public StatsHandler(String year, String round) {
		this.year = year;
		this.round = round;
	}
	
	public void execute(Round round) throws Exception {
		
		RoundStats roundStats = new RoundStats();
		List<TeamStats> stats = new ArrayList<TeamStats>();
		
		leftoverPlayers = new ArrayList<PlayerStats>();
		
		String statsUri = "http://www.afl.com.au/match-centre/" + this.year + "/" + this.round + "/";
		
		for(Fixture game : round.getGames()) {
			
			String gameStr = game.getHomeTeam().toLowerCase() + "-v-" + game.getAwayTeam().toLowerCase();
			String statsUrl = statsUri + gameStr;
			Document doc = Jsoup.parse(new URL(statsUrl).openStream(), "UTF-8", statsUrl);
			
			TeamStats homeTeamStats = new TeamStats();
			homeTeamStats.setTeamId(game.getHomeTeam().toLowerCase());
			homeTeamStats.setTeamStats(getStats("h", doc));
			
			TeamStats awayTeamStats = new TeamStats();
			awayTeamStats.setTeamId(game.getAwayTeam().toLowerCase());
			awayTeamStats.setTeamStats(getStats("a", doc));
						
			stats.add(homeTeamStats);
			stats.add(awayTeamStats);
		}
		
		roundStats.setRoundStats(stats);
		
		writeStatsCsv(roundStats);
		
	}
	
	private List<PlayerStats> getStats(String homeORaway, Document doc) throws Exception {
		
		Element teamStatsTable;
		List<PlayerStats> teamStats = new ArrayList<PlayerStats>();
		
		if(homeORaway.equals("h")) {
			teamStatsTable = doc.getElementById("homeTeam-advanced").getElementsByTag("tbody").get(0);
		} else {
			teamStatsTable = doc.getElementById("awayTeam-advanced").getElementsByTag("tbody").get(0);
		}
		
		Elements teamStatsRecs = teamStatsTable.getElementsByTag("tr");
		for(Element pStatRec : teamStatsRecs) {
			PlayerStats playerStats = new PlayerStats();
			Elements stats = pStatRec.getElementsByTag("td");
			String utfName = stats.get(0).getElementsByClass("full-name").get(0).text();
			//String isoName = new String(utfName.getBytes("UTF-8"), Charset.forName("ISO-8859-1"));

			//playerStats.setName(isoName.replaceAll(" ", " "));
			playerStats.setName(utfName);

			playerStats.setKicks(stats.get(2).text());
			playerStats.setHandballs(stats.get(3).text());
			playerStats.setDisposals(stats.get(4).text());
			playerStats.setMarks(stats.get(9).text());
			playerStats.setHitouts(stats.get(12).text());
			playerStats.setFreesFor(stats.get(17).text());
			playerStats.setFreesAgainst(stats.get(18).text());
			playerStats.setTackles(stats.get(19).text());
			playerStats.setGoals(stats.get(23).text());
			playerStats.setBehinds(stats.get(24).text());
			teamStats.add(playerStats);
		}
		
		return teamStats;
	}
	
	private void writeStatsCsv(RoundStats roundStats) throws Exception {

		Path dir = Paths.get("stats");
		
		String roundPadded = "";
		
		if(this.round.length() < 2) {
			roundPadded = "0" + this.round;
		} else {
			roundPadded = this.round;
		}
		
		Path csvFile = dir.resolve("stats-" + this.year + "-" + roundPadded + ".csv");
		Files.createDirectories(dir);
		CSVWriter csvFileWr = new CSVWriter(Files.newBufferedWriter(csvFile, Charset.forName("ISO-8859-1"), new OpenOption[] {StandardOpenOption.CREATE, StandardOpenOption.WRITE}));
		
		String[] blankline = {""};
		
		csvFileWr.writeNext(blankline);
		List<String[]> rows = createCSVdata(new String[]{"adel", "ADELAIDE"}, new String[]{"bl","BRISBANE"}, new String[]{"carl","CARLTON"}, roundStats);
		csvFileWr.writeAll(rows);
		
		csvFileWr.writeNext(blankline);
		rows = createCSVdata(new String[]{"coll", "COLLINGWOOD"}, new String[]{"ess","ESSENDON"}, new String[]{"fre","FREMANTLE"}, roundStats);
		csvFileWr.writeAll(rows);
		
		csvFileWr.writeNext(blankline);
		rows = createCSVdata(new String[]{"geel", "GEELONG"}, new String[]{"gcfc","GOLD COAST"}, new String[]{"gws","GWS"}, roundStats);
		csvFileWr.writeAll(rows);
		
		csvFileWr.writeNext(blankline);
		rows = createCSVdata(new String[]{"haw", "HAWTHORN"}, new String[]{"melb","MELBOURNE"}, new String[]{"nmfc","NORTH MELBOURNE"}, roundStats);
		csvFileWr.writeAll(rows);
		
		csvFileWr.writeNext(blankline);
		rows = createCSVdata(new String[]{"port", "PORT ADELAIDE"}, new String[]{"rich","RICHMOND"}, new String[]{"stk","ST KILDA"}, roundStats);
		csvFileWr.writeAll(rows);
		
		csvFileWr.writeNext(blankline);
		rows = createCSVdata(new String[]{"syd", "SYDNEY"}, new String[]{"wce","WEST COAST"}, new String[]{"wb","WESTERN BULLDOGS"}, roundStats);
		csvFileWr.writeAll(rows);
		
		csvFileWr.writeNext(blankline);
		csvFileWr.writeNext(blankline);
		csvFileWr.writeNext(blankline);
		csvFileWr.writeNext(blankline);
		
		rows = createLeftoverCSVdata();
		
		csvFileWr.writeAll(rows);
		
		csvFileWr.flush();
		csvFileWr.close();
	}
	
	private List<String[]> createCSVdata(String[] team1, String[] team2, String[] team3, RoundStats roundStats) {
		
		List<String[]> rows = new ArrayList<String[]>();
		String[] header = new String[42];
		
		String teamsFullName[] = {team1[1], team2[1], team3[1]};
		String teamsShortName[] = {team1[0], team2[0], team3[0]};
		
		String columns[] = {"", "", "K", "H", "D", "M", "HO", "FF", "FA", "T", "G", "B", "", "Total"};
		
		for(int i = 0; i < 3; i++) {
			columns[1] = teamsFullName[i];
			for(int j = 0; j < 14; j++) {
				header[(i*14)+j] = columns[j];
			}
		}
		
		rows.add(header);
		
		for(int i = 0; i < 22; i++)  {
			rows.add(new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""});
		}
		
		
		int lastFound = -1;

		for(TeamStats teamStats : roundStats.getRoundStats()) {
			if(teamStats.getTeamId().equals(teamsShortName[0])) {
				lastFound = 0;
			} else if(teamStats.getTeamId().equals(teamsShortName[1])) {
				lastFound = 1;
			} else if(teamStats.getTeamId().equals(teamsShortName[2])) {
				lastFound = 2;
			} else {
				lastFound = -1;
			}
			
			if(lastFound >= 0) {
				int i = 1;
				

				for(PlayerStats playerStats : teamStats.getTeamStats()) {
					if(i <= 22) {
						String[] row = rows.get(i);
						
						row[(lastFound*14)+1] = playerStats.getName();
						row[(lastFound*14)+2] = playerStats.getKicks();
						row[(lastFound*14)+3] = playerStats.getHandballs();
						row[(lastFound*14)+4] = playerStats.getDisposals();
						row[(lastFound*14)+5] = playerStats.getMarks();
						row[(lastFound*14)+6] = playerStats.getHitouts();
						row[(lastFound*14)+7] = playerStats.getFreesFor();
						row[(lastFound*14)+8] = playerStats.getFreesAgainst();
						row[(lastFound*14)+9] = playerStats.getTackles();
						row[(lastFound*14)+10] = playerStats.getGoals();
						row[(lastFound*14)+11] = playerStats.getBehinds();
						
						rows.set(i, row);
					} else {
						leftoverPlayers.add(playerStats);
					}
					
					i++;
				}
			}
		}
		
		return rows;
	}
	
	private List<String[]> createLeftoverCSVdata() {
		
		List<String[]> rows = new ArrayList<String[]>();
		
		for(PlayerStats playerStats : leftoverPlayers) {
			String[] row = new String[11];
			
			row[0] = playerStats.getName();
			row[1] = playerStats.getKicks();
			row[2] = playerStats.getHandballs();
			row[3] = playerStats.getDisposals();
			row[4] = playerStats.getMarks();
			row[5] = playerStats.getHitouts();
			row[6] = playerStats.getFreesFor();
			row[7] = playerStats.getFreesAgainst();
			row[8] = playerStats.getTackles();
			row[9] = playerStats.getGoals();
			row[10] = playerStats.getBehinds();
			
			rows.add(row);
		}
		
		return rows;
	}
}
