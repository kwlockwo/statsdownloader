package net.dfl.statsdownloder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import au.com.bytecode.opencsv.CSVWriter;


public class DownloadStats {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 * @throws Exception 
	 */
	
	public static List<String[]> getStats(String homeORaway, Document doc) throws UnsupportedEncodingException {
		
		Element teamStatsTable;
		List<String[]> teamStats = new ArrayList<String[]>();
		
		if(homeORaway.equals("h")) {
			teamStatsTable = doc.getElementById("homeTeam-advanced").getElementsByTag("tbody").get(0);
		} else {
			teamStatsTable = doc.getElementById("awayTeam-advanced").getElementsByTag("tbody").get(0);
		}
		
		Elements teamStatsRecs = teamStatsTable.getElementsByTag("tr");
		for(Element pStatRec : teamStatsRecs) {
			String[] playerStats = new String[11];
			Elements stats = pStatRec.getElementsByTag("td");
			String utfName = stats.get(0).getElementsByClass("full-name").get(0).text();
			String isoName = new String(utfName.getBytes("UTF-8"), Charset.forName("ISO-8859-1"));
			//playerStats[0] = isoName.replaceAll("[^\\p{ASCII}]", "-");
			playerStats[0] = isoName.replaceAll(" ", " ");
			//playerStats[0] = isoName;
			
			//String name = stats.get(0).getElementsByClass("full-name").get(0).text().replace("[�]", "")
					
			//playerStats[0] = stats.get(0).getElementsByClass("full-name").get(0).text().replace("[�]", "");
			//playerStats[0] = stats.get(0).getElementsByClass("full-name").get(0).text();
			playerStats[1] = stats.get(2).text();
			playerStats[2] = stats.get(3).text();
			playerStats[3] = stats.get(4).text();
			playerStats[4] = stats.get(9).text();
			playerStats[5] = stats.get(12).text();
			playerStats[6] = stats.get(17).text();
			playerStats[7] = stats.get(18).text();
			playerStats[8] = stats.get(19).text();
			playerStats[9] = stats.get(23).text();
			playerStats[10] = stats.get(24).text();
			teamStats.add(playerStats);
		}
		
		return teamStats;
	}
	
	public static void writeStatsCsv(String game, String year, String round, List<String[]> homeTeamStats,
								List<String[]> awayTeamStats) throws Exception {
		
		Path dir = Paths.get("stats/" + year + "/" + round);
		Path csvFile = dir.resolve(game + ".csv");
		
		Files.createDirectories(dir);
		
		String[] teams = game.split("-v-");
			
		CSVWriter csvFileWr = new CSVWriter(Files.newBufferedWriter(csvFile, Charset.forName("ISO-8859-1"), new OpenOption[] {StandardOpenOption.CREATE, StandardOpenOption.WRITE}));
			
		String[] header = new String[1];
		header[0] = teams[0];
			
		String[] blankline = {""};
			
		csvFileWr.writeNext(header);
		csvFileWr.writeAll(homeTeamStats);
		csvFileWr.writeNext(blankline);
			
		header[0] = teams[1];
		
		csvFileWr.writeNext(header);
		csvFileWr.writeAll(awayTeamStats);
		
		csvFileWr.flush();
		csvFileWr.close();
	}
	
	
	public static void main(String[] args) throws Exception {
		
		
		Properties props = new Properties();
		
		props.load(new FileInputStream("downloadStats.properties"));
		
		String proxy = props.getProperty("proxy");
		
		if(proxy.equals("Y")) {
			
			System.setProperty("http.proxyHost", props.getProperty("proxyHost"));
			System.setProperty("http.proxyPort", props.getProperty("proxyPort"));
			System.setProperty("http.proxyUser", props.getProperty("proxyUser"));
			System.setProperty("http.proxyPassword", props.getProperty("proxyPassword"));
			
			/*
			System.setProperty("http.proxyHost", "wpproxy");
			System.setProperty("http.proxyPort", props.getProperty("proxyPort"));
			System.setProperty("http.proxyUser", props.getProperty("proxyUser"));
			System.setProperty("http.proxyPassword", props.getProperty("proxyPassword"));
			*/
		}
		
		String round = props.getProperty("round");
		String year = props.getProperty("year");
		
		File gamesini = new File(DownloadStats.class.getResource("games.ini").getFile());
		List<String> games = FileUtils.readLines(gamesini);
		
		String statsUri = "http://www.afl.com.au/match-centre/" + year + "/" + round + "/";
		
		for(String game : games) {
			System.out.println("URL: " + statsUri + game);
			String statsUrl = statsUri + game;
			Document doc = Jsoup.parse(new URL(statsUrl).openStream(), "UTF-8", statsUrl);
			List<String[]> homeTeamStats = getStats("h", doc);
			List<String[]> awayTeamStats = getStats("a", doc);
			writeStatsCsv(game, year, round, homeTeamStats, awayTeamStats);
		}
	}
}
