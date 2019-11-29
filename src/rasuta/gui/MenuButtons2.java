/*	Creator: Vajda
 * 	Datum:
 * 	Datum importovanja: 08.02.2019
 *  Version: 1.0
 * 
 */
package rasuta.gui;

import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;


public class MenuButtons2 extends JPanel {
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;
	private JLabel lblNewLabel_4;
	private JLabel lblNewLabel_5;
	private JLabel lblNewLabel_6;
	private JLabel lblNewLabel_7;
	
	
	public MenuButtons2() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{120, 0};
		gridBagLayout.rowHeights = new int[]{31, 31, 31, 31, 31, 31, 31, 31, 0};
		gridBagLayout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		
		
		lblNewLabel_1 = new JLabel("");
		
		lblNewLabel_1.setToolTipText("Formiraj datoteku\r\n");
		lblNewLabel_1.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/datAdd.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 0;
		add(lblNewLabel_1, gbc_lblNewLabel_1);
		lblNewLabel_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				lblNewLabel_1.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/datAdd.png")).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
				
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				lblNewLabel_1.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/datAdd.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
			}});
		
		
		lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setToolTipText("Ucitaj datoteku");
		lblNewLabel_2.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/open.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 1;
		add(lblNewLabel_2, gbc_lblNewLabel_2);
		lblNewLabel_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				lblNewLabel_2.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/open.png")).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				lblNewLabel_2.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/open.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
			}});
			
			lblNewLabel_3 = new JLabel("");
			lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
			lblNewLabel_3.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/load.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
			GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
			gbc_lblNewLabel_3.fill = GridBagConstraints.BOTH;
			gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 0);
			gbc_lblNewLabel_3.gridx = 0;
			gbc_lblNewLabel_3.gridy = 2;
			add(lblNewLabel_3, gbc_lblNewLabel_3);
			lblNewLabel_3.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent arg0) {
					lblNewLabel_3.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/load.png")).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					lblNewLabel_3.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/load.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
				}});
		
			
			lblNewLabel_4 = new JLabel("");
			lblNewLabel_4.setToolTipText("Unesi slog");
			lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
			lblNewLabel_4.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
			GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
			gbc_lblNewLabel_4.fill = GridBagConstraints.BOTH;
			gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 0);
			gbc_lblNewLabel_4.gridx = 0;
			gbc_lblNewLabel_4.gridy = 3;
			add(lblNewLabel_4, gbc_lblNewLabel_4);
			lblNewLabel_4.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent arg0) {
					lblNewLabel_4.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png")).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					lblNewLabel_4.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
				}});
		
		
		lblNewLabel_5 = new JLabel("");
		lblNewLabel_5.setToolTipText("Fizicko brisanje");
		lblNewLabel_5.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_5.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/x.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_5.gridx = 0;
		gbc_lblNewLabel_5.gridy = 4;
		add(lblNewLabel_5, gbc_lblNewLabel_5);
		lblNewLabel_5.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				lblNewLabel_5.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/x.png")).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				lblNewLabel_5.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/x.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
			}});
		
		
		lblNewLabel_6 = new JLabel("");
		lblNewLabel_6.setToolTipText("Logicko brisanje");
		lblNewLabel_6.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_6.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/xdash.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_6.gridx = 0;
		gbc_lblNewLabel_6.gridy = 5;
		add(lblNewLabel_6, gbc_lblNewLabel_6);
		lblNewLabel_6.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				lblNewLabel_6.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/xdash.png")).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				lblNewLabel_6.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/xdash.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
			}});
		
		
		lblNewLabel_7 = new JLabel("");
		lblNewLabel_7.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_7.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/edit.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
		GridBagConstraints gbc_lblNewLabel_7 = new GridBagConstraints();
		gbc_lblNewLabel_7.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_7.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_7.gridx = 0;
		gbc_lblNewLabel_7.gridy = 6;
		add(lblNewLabel_7, gbc_lblNewLabel_7);
		lblNewLabel_7.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				lblNewLabel_7.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/edit.png")).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				lblNewLabel_7.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/edit.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
			}});
		
		
		lblNewLabel = new JLabel("");
		lblNewLabel.setToolTipText("Pronadji slog");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 7;
		add(lblNewLabel, gbc_lblNewLabel);
		lblNewLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				lblNewLabel.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search.png")).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				lblNewLabel.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
			}});

	}
	
	

}

