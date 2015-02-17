package net.dfl.statsdownloder.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.dfl.statsdownloder.model.struct.Fixture;
import net.dfl.statsdownloder.model.struct.Round;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FixtureHandler {
	
	

	public Round execute(String year, String roundNo) throws Exception {
		
		String paddedRoundNo = "";
		
		if(roundNo.length() < 2) {
			paddedRoundNo = "0" + roundNo;
		}
		
		String fixtureUrl = "http://www.afl.com.au/fixture?roundId=CD_R" + year + "014" + paddedRoundNo + "#tround";
		
		Document doc = Jsoup.parse(new URL(fixtureUrl).openStream(), "UTF-8", fixtureUrl);
		
		Elements matches = doc.getElementById("tround").getElementsByTag("tbody").select(".team-logos");
		
		Round round = new Round();
		List<Fixture> games = new ArrayList<Fixture>();
		
		for(Element match : matches) {
			Fixture fixture = new Fixture();
			fixture.setHomeTeam(match.select(".home").text());
			fixture.setAwayTeam(match.select(".away").text());
			
			games.add(fixture);
		}
		
		round.setGames(games);
		return round;
	}

}
