package net.dfl.statsdownloader.model;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;
import ch.qos.logback.classic.Logger;
import net.dfl.statsdownloader.model.struct.Fixture;
import net.dfl.statsdownloader.model.struct.PlayerStats;
import net.dfl.statsdownloader.model.struct.Round;
import net.dfl.statsdownloader.model.struct.RoundStats;
import net.dfl.statsdownloader.model.struct.TeamStats;

public class StatsHandler {
	
	Logger logger = (Logger) LoggerFactory.getLogger("statsDownloaderLogger");
	
	private String year;
	private String round;
	
	//List<PlayerStats> leftoverPlayers;
	
	public StatsHandler(String year, String round) {

		this.year = year;
		this.round = round;
	}
	
	public void execute(Round round) throws Exception {
		
		logger.info("Handling Stats Download for: year={}, round={}", year, this.round);
		
		RoundStats roundStats = new RoundStats();
		List<TeamStats> stats = new ArrayList<TeamStats>();
		
		//leftoverPlayers = new ArrayList<PlayerStats>();
		
		String statsUri = "http://www.afl.com.au/match-centre/" + this.year + "/" + this.round + "/";
				
		ExecutorService executor = Executors.newFixedThreadPool(10);
		List<Future<List<TeamStats>>> futures = new ArrayList<>();
		
		for(Fixture game : round.getGames()) {
			
			try {
				String gameStr = game.getHomeTeam().toLowerCase() + "-v-" + game.getAwayTeam().toLowerCase();
				String statsUrl = statsUri + gameStr;
				
				logger.info("Stats URL: {}", statsUrl);
				
				if(System.getProperty("app.debug").equals("Y")) {
					logger.debug("Creating Thread");
				}
				Callable<List<TeamStats>> callable = new StatsCallable(statsUrl, game.getHomeTeam().toLowerCase(), game.getHomeTeam().toLowerCase());
				
				if(System.getProperty("app.debug").equals("Y")) {
					logger.debug("Executing Thread Thread");
				}
				Future<List<TeamStats>> future = executor.submit(callable);
				futures.add(future);
				
			} catch (Exception ex) {
				logger.error("Error: ", ex);
			} finally {} //ignore errors
		}
		
		logger.info("Saving stats");
		for(Future<List<TeamStats>> future : futures) {
			stats.addAll(future.get());
			if(System.getProperty("app.debug").equals("Y")) {
				logger.debug("Stats from tread: {}", future.get());
			}
		}
		
		roundStats.setRoundStats(stats);
		if(System.getProperty("app.debug").equals("Y")) {
			logger.debug("Round stats: {}", roundStats);
		}
		
		
		//writeStatsCsv(roundStats);
		
		logger.info("Writing CSV");
		writeRobStatsCsv(roundStats);
		
		logger.info("Stats Download Handler Completed");
	}
	
	/*
	private List<PlayerStats> getStats(String homeORaway, WebDriver driver) throws Exception {
		
		List<PlayerStats> teamStats = new ArrayList<PlayerStats>();
		
		driver.findElement(By.cssSelector("a[href='#full-time-stats']")).click();
		driver.findElement(By.cssSelector("a[href='#advanced-stats']")).click();

		List<WebElement> statsRecs;
		
		if(homeORaway.equals("h")) {
			statsRecs = driver.findElement(By.id("homeTeam-advanced")).findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
		} else {
			statsRecs = driver.findElement(By.id("awayTeam-advanced")).findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
		}
		
		for(WebElement statsRec : statsRecs) {
			PlayerStats playerStats = new PlayerStats();
			
			List<WebElement> stats = statsRec.findElements(By.tagName("td"));

			playerStats.setName(stats.get(0).findElements(By.tagName("span")).get(1).getText());
			playerStats.setKicks(stats.get(2).getText());
			playerStats.setHandballs(stats.get(3).getText());
			playerStats.setDisposals(stats.get(4).getText());
			playerStats.setMarks(stats.get(9).getText());
			playerStats.setHitouts(stats.get(12).getText());
			playerStats.setFreesFor(stats.get(17).getText());
			playerStats.setFreesAgainst(stats.get(18).getText());
			playerStats.setTackles(stats.get(19).getText());
			playerStats.setGoals(stats.get(23).getText());
			playerStats.setBehinds(stats.get(24).getText());
			teamStats.add(playerStats);
		}
		
		return teamStats;
	}
	*/
	
	private void writeRobStatsCsv(RoundStats roundStats) throws Exception {
		Path dir = Paths.get("stats");
		
		String roundPadded = "";
		
		if(this.round.length() < 2) {
			roundPadded = "0" + this.round;
		} else {
			roundPadded = this.round;
		}
		
		Path csvFile = dir.resolve("stats-" + this.year + "-" + roundPadded + ".csv");
		
		logger.info("CSV File: {}", dir + "/" + csvFile);
		
		Files.createDirectories(dir);
		CSVWriter csvFileWr = new CSVWriter(Files.newBufferedWriter(csvFile, Charset.forName("Cp1252"), new OpenOption[] {StandardOpenOption.CREATE, StandardOpenOption.WRITE}));
		
		List<String[]> rows = new ArrayList<String[]>();
		
		if(System.getProperty("app.debug").equals("Y")) {
			logger.debug("Round stats to be written: {}", roundStats);
		}
		
		for(TeamStats teamStats : roundStats.getRoundStats()) {
			for(PlayerStats playerStats : teamStats.getTeamStats()) {
				
				if(System.getProperty("app.debug").equals("Y")) {
					logger.debug("Player to be written: {}", playerStats);
				}		
				
				rows.add(new String[]{playerStats.getName(),
									  playerStats.getDisposals(), 
									  playerStats.getMarks(),  
									  playerStats.getHitouts(), 
									  playerStats.getFreesFor(), 
									  playerStats.getFreesAgainst(),
									  playerStats.getTackles(),
									  playerStats.getGoals()});
				
				if(System.getProperty("app.debug").equals("Y")) {
					logger.debug("CSV Row: {}", (Object[])rows.get(rows.size()-1));
				}
			}
		}
		
		csvFileWr.writeAll(rows);
		csvFileWr.flush();
		csvFileWr.close();
		logger.info("CSV File written");
	}
	
	/*
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
	*/
}
