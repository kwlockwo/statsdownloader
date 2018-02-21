package net.dfl.statsdownloader.model;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import io.github.bonigarcia.wdm.PhantomJsDriverManager;
import net.dfl.statsdownloader.model.struct.Fixture;
import net.dfl.statsdownloader.model.struct.Round;

public class FixtureHandler {
	
	public Round execute(String year, String roundNo) throws Exception {
		
		Logger logger = (Logger) LoggerFactory.getLogger("statsDownloaderLogger");
		
		String paddedRoundNo = "";
		
		if(roundNo.length() < 2) {
			paddedRoundNo = "0" + roundNo;
		} else {
			paddedRoundNo = roundNo;
		}
		
		String fixtureUrl = "http://www.afl.com.au/fixture?roundId=CD_R" + year + "014" + paddedRoundNo + "#tround";
		
		PhantomJsDriverManager.getInstance().setup();
		WebDriver driver = new PhantomJSDriver();
		
		logger.info("Loading fixture from: {}", fixtureUrl);
		
		driver.get(fixtureUrl);
		
		List<WebElement> webFixtures = driver.findElement(By.id("tround")).findElement(By.tagName("tbody")).findElements(By.className("team-logos"));
		
		Round round = new Round();
		List<Fixture> games = new ArrayList<Fixture>();
		
		for(WebElement webFixture : webFixtures) {
			Fixture fixture = new Fixture();
			fixture.setHomeTeam(webFixture.findElements(By.className("home")).get(0).getText());
			fixture.setAwayTeam(webFixture.findElements(By.className("away")).get(0).getText());
			
			if(System.getProperty("app.debug").equals("Y")) {
				logger.debug("Fixture: {}", fixture);
			}
			
			games.add(fixture);
		}
		
		driver.quit();
		
		round.setGames(games);
		
		if(System.getProperty("app.debug").equals("Y")) {
			logger.debug("Round Games: {}", round);
		}
		
		logger.info("Fixtures Loaded");
		
		return round;
	}
}
