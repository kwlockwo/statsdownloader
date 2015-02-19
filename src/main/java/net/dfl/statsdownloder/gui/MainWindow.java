package net.dfl.statsdownloder.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jsoup.nodes.Element;

import net.dfl.statsdownloder.model.struct.Fixture;
import net.dfl.statsdownloder.model.struct.Round;
import net.miginfocom.swing.MigLayout;

public class MainWindow implements Observer, ActionListener {

	private JFrame mainWindow;
	
	private JButton getFixtureBtn;
	private JButton getStatsBtn;
	private JButton settingsBtn;
	
	private JTextField yearInputTxt;
	private JTextField roundInputTxt;
	
	private JTextField httpProxyHostTxt;
	private JTextField httpProxyPortTxt;
	private JTextField httpProxyUserTxt;
	private JPasswordField httpProxyPassTxt;
	
	private JTextField outputDirTxt;
	private JButton openDirBtn;
	
	private JFileChooser fileChooser;
	
	private JComboBox<String> proxyYesNoBox;
	
	private JButton okBtn;
	private JButton cancelBtn;
	

	private List<JLabel> games;
	
	public MainWindow() {
		mainWindow = new JFrame();
		
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		mainWindow.setTitle("DFL Stats Downloader");
		mainWindow.setBounds(100, 100, 300, 300);
		mainWindow.setResizable(false);
		
		Container contentPane = mainWindow.getContentPane();
		//contentPane.setLayout(new BorderLayout(0, 0));
		
		contentPane.setLayout(new CardLayout());
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(0, 0));
		
		
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
		
		//contentPane.add(p, BorderLayout.NORTH);
		mainPanel.add(p, BorderLayout.NORTH);
		
		
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
		
		//contentPane.add(p, BorderLayout.CENTER);
		mainPanel.add(p, BorderLayout.CENTER);
		
		p = new JPanel();
		
		getFixtureBtn = new JButton("Get Fixture");
		getStatsBtn = new JButton("Get Stats");
		
		settingsBtn = new JButton("Settings");
		settingsBtn.addActionListener(this);
		
		p.add(getFixtureBtn);
		p.add(getStatsBtn);
		p.add(settingsBtn);
		
		
		//contentPane.add(p, BorderLayout.SOUTH);
		mainPanel.add(p, BorderLayout.SOUTH);
		
		contentPane.add(mainPanel, "MAINPANEL");
		
		JPanel settingsPanel = new JPanel();
		settingsPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel borderPanel = new JPanel();
		TitledBorder title;
		title = BorderFactory.createTitledBorder("HTTP Proxy Settings");
		borderPanel.setBorder(title);
		
		p = new JPanel();
		p.setLayout(new MigLayout());
		
		final DefaultComboBoxModel<String> yesNo = new DefaultComboBoxModel<String>();
		
		yesNo.addElement("Yes");
		yesNo.addElement("No");
		
		//String[] yesNo = {"Yes", "No"};
		proxyYesNoBox = new JComboBox<String>(yesNo);
		
		if(System.getProperty("http.proxy").equals("Y")) {
			proxyYesNoBox.setSelectedIndex(0);
		} else {
			proxyYesNoBox.setSelectedIndex(1);
		}
		
		proxyYesNoBox.setActionCommand("proxyYesNoBox");
		proxyYesNoBox.addActionListener(this);
		
		l = new JLabel("Enable Proxy:");
		l.setLabelFor(proxyYesNoBox);
		p.add(l, "align label, alignx trailing");
		p.add(proxyYesNoBox, "wrap");
		
		l = new JLabel("Proxy Host:", JLabel.TRAILING);
		httpProxyHostTxt = new JTextField(15);
		l.setLabelFor(httpProxyHostTxt);
		p.add(l, "align label, alignx trailing");
		p.add(httpProxyHostTxt, "wrap");
		
		l = new JLabel("Proxy Port:", JLabel.TRAILING);
		httpProxyPortTxt = new JTextField(15);
		l.setLabelFor(httpProxyPortTxt);
		p.add(l, "align label, alignx trailing");
		p.add(httpProxyPortTxt, "wrap");
		
		l = new JLabel("Proxy Username:", JLabel.TRAILING);
		httpProxyUserTxt = new JTextField(15);
		l.setLabelFor(httpProxyUserTxt);
		p.add(l, "align label, alignx trailing");
		p.add(httpProxyUserTxt, "wrap");

		l = new JLabel("Proxy Password:", JLabel.TRAILING);
		httpProxyPassTxt = new JPasswordField(15);
		l.setLabelFor(httpProxyPassTxt);
		p.add(l, "align label, alignx trailing");
		p.add(httpProxyPassTxt, "wrap, wrap");
		
		if(System.getProperty("http.proxy").equals("Y")) {
			httpProxyHostTxt.setText(System.getProperty("http.proxyHost"));
			httpProxyPortTxt.setText(System.getProperty("http.proxyPort"));
			httpProxyUserTxt.setText(System.getProperty("http.proxyUser"));
			httpProxyPassTxt.setText(System.getProperty("http.proxyPassword"));
		} else {
			httpProxyHostTxt.setText(System.getProperty("http.proxyHost.disabled"));
			httpProxyPortTxt.setText(System.getProperty("http.proxyPort.disabled"));
			httpProxyUserTxt.setText(System.getProperty("http.proxyUser.disabled"));
			httpProxyPassTxt.setText(System.getProperty("http.proxyPassword.disabled"));
			
			httpProxyHostTxt.setEditable(false);
			httpProxyPortTxt.setEditable(false);
			httpProxyUserTxt.setEditable(false);
			httpProxyPassTxt.setEditable(false);
		}
		
		borderPanel.add(p);
		settingsPanel.add(borderPanel, BorderLayout.NORTH);
		
		p = new JPanel();		

		borderPanel = new JPanel();
		title = BorderFactory.createTitledBorder("Output Folder");
		borderPanel.setBorder(title);
		
		l = new JLabel("Folder:", JLabel.TRAILING);
		outputDirTxt = new JTextField(16);
		l.setLabelFor(outputDirTxt);
		p.add(l, "align label, alignx trailing");
		p.add(outputDirTxt, "");
		
		openDirBtn = new JButton("...");
		openDirBtn.addActionListener(this);
		p.add(openDirBtn, "wrap");
		
		
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		//settingsPanel.add(p, BorderLayout.NORTH);
		borderPanel.add(p);
		settingsPanel.add(borderPanel, BorderLayout.CENTER);
		
		p = new JPanel();
		
		okBtn = new JButton("OK");
		cancelBtn = new JButton("Cancel");
		
		okBtn.addActionListener(this);
		cancelBtn.addActionListener(this);
		
		p.add(okBtn);
		p.add(cancelBtn);
		
		settingsPanel.add(p, BorderLayout.SOUTH);
		
		contentPane.add(settingsPanel, "SETTINGS");
		
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

	public void actionPerformed(ActionEvent action) {
		
		String test = action.getActionCommand();
		
		if(action.getActionCommand().equals("Settings")) {
			CardLayout cards = (CardLayout)mainWindow.getContentPane().getLayout();
			cards.show(mainWindow.getContentPane(), "SETTINGS");
		}
		if(action.getActionCommand().equals("OK")||action.getActionCommand().equals("Cancel")) {
			CardLayout cards = (CardLayout)mainWindow.getContentPane().getLayout();
			cards.show(mainWindow.getContentPane(), "MAINPANEL");
		}
		if(action.getActionCommand().equals("...")) {
			int returnVal = fileChooser.showOpenDialog(mainWindow);
			
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                outputDirTxt.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
		}
		if(action.getActionCommand().equals("proxyYesNoBox")) {
			JComboBox cb = (JComboBox)action.getSource();
	        String yesNo = (String)cb.getSelectedItem();
			
	        if(yesNo.equals("Yes")) {
				httpProxyHostTxt.setEditable(true);
				httpProxyPortTxt.setEditable(true);
				httpProxyUserTxt.setEditable(true);
				httpProxyPassTxt.setEditable(true);
	        } else {
				httpProxyHostTxt.setEditable(false);
				httpProxyPortTxt.setEditable(false);
				httpProxyUserTxt.setEditable(false);
				httpProxyPassTxt.setEditable(false);
	        }
		}
	}
	
}
