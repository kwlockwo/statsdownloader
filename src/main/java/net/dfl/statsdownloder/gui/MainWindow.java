package net.dfl.statsdownloder.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.jsoup.nodes.Element;

import net.dfl.statsdownloder.model.struct.Fixture;
import net.dfl.statsdownloder.model.struct.Round;
import net.miginfocom.swing.MigLayout;

public class MainWindow implements Observer {

	private JFrame mainWindow;
	
	private JButton getFixtureBtn;
	private JButton getStatsBtn;
	
	private JTextField yearInputTxt;
	private JTextField roundInputTxt;

	private List<JLabel> games;
	
	public MainWindow() {
		mainWindow = new JFrame();
		
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		mainWindow.setTitle("DFL Stats Downloader");
		mainWindow.setBounds(100, 100, 250, 300);
		mainWindow.setResizable(false);
		
		Container contentPane = mainWindow.getContentPane();
		contentPane.setLayout(new BorderLayout(0, 0));
		
		
		JPanel p = new JPanel();
		p.setLayout(new MigLayout());
		
		JLabel l = new JLabel("Year:");
		yearInputTxt = new JTextField(5);
		l.setLabelFor(yearInputTxt);
		p.add(l, "align label, alignx trailing");
		p.add(yearInputTxt, "wrap");
				
		l = new JLabel("Round:", JLabel.TRAILING);
		roundInputTxt = new JTextField(5);
		l.setLabelFor(roundInputTxt);
		p.add(l, "align label, alignx trailing");
		p.add(roundInputTxt, "wrap");
		
		contentPane.add(p, BorderLayout.NORTH);
		
		
		/*
		JPanel p = new JPanel(new SpringLayout());
		JLabel l = new JLabel("Year:");
		yearInputTxt = new JTextField(5);
		l.setLabelFor(yearInputTxt);
		p.add(l);
		p.add(yearInputTxt);
		
		l = new JLabel("Round:", JLabel.TRAILING);
		roundInputTxt = new JTextField(5);
		l.setLabelFor(roundInputTxt);
		p.add(l);
		p.add(roundInputTxt);
		
		SpringUtilities.makeCompactGrid(p, 2, 2, 6, 6, 6, 6);
		
		contentPane.add(p, BorderLayout.NORTH);
		*/
		
		p = new JPanel();
		p.setBorder(new EmptyBorder(0, 25, 10, 25));
		p.setLayout(new GridLayout(9, 4, 0, 0));
		
		games = new ArrayList<JLabel>();
		
		for(int i=0;i<=8;i++) {
			//l = new JLabel("v", JLabel.CENTER);
			l = new JLabel("v");
			l.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			l.setHorizontalAlignment(SwingConstants.CENTER);
			p.add(l);
			games.add(l);
		}
		
		contentPane.add(p, BorderLayout.CENTER);
		
		p = new JPanel();
		
		getFixtureBtn = new JButton("Get Fixture");
		getStatsBtn = new JButton("Get Stats");
		p.add(getFixtureBtn);
		p.add(getStatsBtn);
		
		contentPane.add(p, BorderLayout.SOUTH);
		
		//mainWindow.pack();
		//mainWindow.addWindowListener(new CloseListener());
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setVisible(true);
	}
	
	public void update(Observable obs, Object obj) {
		int i = 0;
		
		if(obj != null) {
			for(Fixture game : ((Round)obj).getGames()) {
				games.get(i).setText(game.getHomeTeam() + " v " + game.getAwayTeam());
				i++;
				getStatsBtn.setEnabled(true);
			}
		} else {
			getStatsBtn.setEnabled(false);
		}
	}
	
	public void updateFixtures(Round round) {
		int i = 0;
		for(Fixture game : round.getGames()) {
			games.get(i).setText(game.getHomeTeam() + " v " + game.getAwayTeam());
			i++;
		}
	}
	
	public void toggleWorkingCurser(boolean working) {
		if(working) {
			Cursor waitCursoir = new Cursor(Cursor.WAIT_CURSOR);
			mainWindow.setCursor(waitCursoir);
		} else {
			Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
			mainWindow.setCursor(defaultCursor);
		}
	}
	
	public JTextField getYearInputTxt() {
		return yearInputTxt;
	}

	public void setYearInputTxt(JTextField yearInputTxt) {
		this.yearInputTxt = yearInputTxt;
	}

	public JTextField getRoundInputTxt() {
		return roundInputTxt;
	}

	public void setRoundInputTxt(JTextField roundInputTxt) {
		this.roundInputTxt = roundInputTxt;
	}
	
	public void addFixtureHandler(ActionListener listener) {
		getFixtureBtn.addActionListener(listener);
	}
	
	public void addStatsHandler(ActionListener listener) {
		getStatsBtn.addActionListener(listener);
	}
	
	public static class CloseListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			e.getWindow().setVisible(false);
			System.exit(0);
		} //windowClosing()
	} //CloseListene
	
}
