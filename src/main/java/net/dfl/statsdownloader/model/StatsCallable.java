package net.dfl.statsdownloader.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import io.github.bonigarcia.wdm.PhantomJsDriverManager;
import net.dfl.statsdownloader.model.struct.PlayerStats;
import net.dfl.statsdownloader.model.struct.TeamStats;

public class StatsCallable implements Callable<List<TeamStats>> {
	
	Logger logger = (Logger) LoggerFactory.getLogger("statsDownloaderLogger");
	
	String url;
	String homeTeam;
	String awayTeam;
	
	public StatsCallable(String threadUrl, String hTeam, String aTeam) {
		url = threadUrl;
		homeTeam = hTeam;
		awayTeam = aTeam;
	}
	
	public List<TeamStats> call() {
		
		PhantomJsDriverManager.getInstance().setup();
		WebDriver driver = new PhantomJSDriver();
		
		List<TeamStats> stats = new ArrayList<TeamStats>();
					
		try {				
			driver.get(url);
			
			logger.info("Getting home team stats: {}", homeTeam);
			TeamStats homeTeamStats = new TeamStats();
			homeTeamStats.setTeamId(homeTeam);
			homeTeamStats.setTeamStats(getStats("h", driver));
			
			logger.info("Getting away team stats: {}", awayTeam);
			TeamStats awayTeamStats = new TeamStats();
			awayTeamStats.setTeamId(awayTeam);
			awayTeamStats.setTeamStats(getStats("a", driver));
						
			stats.add(homeTeamStats);
			stats.add(awayTeamStats);
			
		} catch (Exception e) {} finally {} //ignore errors

		driver.quit();
		
		if(System.getProperty("app.debug").equals("Y")) {
			logger.debug("All stats: {}", stats);
		}
		
		logger.info("Stats have been downloaded");
		
		return stats;
	}
	
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
			
			if(System.getProperty("app.debug").equals("Y")) {
				logger.debug("Player stats: {}", playerStats);
			}
		}
		
		if(System.getProperty("app.debug").equals("Y")) {
			logger.debug("Team stats: {}", teamStats);
		}
		
		return teamStats;
	}

}
