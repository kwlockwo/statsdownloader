package net.dfl.statsdownloder.gui;

import java.awt.BorderLayout;
import java.awt.Container;
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
import javax.swing.border.EtchedBorder;

public class MainWindow implements Observer {

	private JButton getFixture;
	private JButton getStats;
	
	private JLabel game1;
	private JLabel game2;
	private JLabel game3;
	private JLabel game4;
	private JLabel game5;
	private JLabel game6;
	private JLabel game7;
	private JLabel game8;
	private JLabel game9;
	
	public MainWindow() {
		JFrame mainWindow = new JFrame();
		
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		mainWindow.setTitle("DFL Stats Downloader");
		mainWindow.setSize(300,250);
		
		//mainWindow.setLocationRelativeTo(null);
		
		//Container mwp =  mainWindow.getContentPane();
		//mwp.setLayout(mgr);
		
		//mainWindow.setLayout(new BoxLayout(mainWindow, BoxLayout.PAGE_AXIS));
		
		JPanel p = new JPanel(new SpringLayout());
		JLabel l = new JLabel("Year:", JLabel.TRAILING);
		p.add(l);
		JTextField textFeild = new JTextField(5);
		l.setLabelFor(textFeild);
		p.add(textFeild);
		
		l = new JLabel("Round:", JLabel.TRAILING);
		p.add(l);
		textFeild = new JTextField(5);
		l.setLabelFor(textFeild);
		p.add(textFeild);
		
		Container contentPane = mainWindow.getContentPane();
		contentPane.add(p,BorderLayout.CENTER);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		
		for(int i=0;i<=8;i++) {
			l = new JLabel("v", JLabel.CENTER);
			l.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			p.add(l);
		}
		
		contentPane.add(p,BorderLayout.NORTH);
		
		p = new JPanel();
		
		getFixture = new JButton("Get Fixture");
		getStats = new JButton("GetStats");
		
		p.add(getFixture);
		p.add(getStats);
		
		contentPane.add(p,BorderLayout.SOUTH);
		
		mainWindow.pack();
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setVisible(true);
	}
	
	public void update(Observable obs, Object obj) {}
	
}
