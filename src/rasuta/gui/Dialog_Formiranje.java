package rasuta.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import rasuta.model.Rasuta;
import rasuta.model.RasutaSaPrekoraciocem;

import javax.swing.JCheckBox;

public class Dialog_Formiranje extends JDialog {

	private JPanel contentPane;
	private JTextField textField;
	private ButtonGroup group;
	private ButtonGroup group2;
	private JTextField textField_1;
	private JTextField textField_2;
	private RasutaSaPrekoraciocem rasuta;
	public JCheckBox chckbxNewCheckBox;
	public JButton btnNewButton;
	
	/**
	 * Create the frame.
	 */
	public Dialog_Formiranje() {
		setTitle("Unosenje sloga");
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setModal(true);
		setBounds(100, 100, 452, 435);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{10, 87, 35, 205, 0, 0};
		gbl_contentPane.rowHeights = new int[]{26, 35, 72, 105, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblLocation = new JLabel("Location:");
		GridBagConstraints gbc_lblLocation = new GridBagConstraints();
		gbc_lblLocation.anchor = GridBagConstraints.WEST;
		gbc_lblLocation.fill = GridBagConstraints.VERTICAL;
		gbc_lblLocation.insets = new Insets(0, 0, 5, 5);
		gbc_lblLocation.gridx = 1;
		gbc_lblLocation.gridy = 1;
		contentPane.add(lblLocation, gbc_lblLocation);
		
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.fill = GridBagConstraints.BOTH;
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.gridx = 3;
		gbc_textField.gridy = 1;
		contentPane.add(textField, gbc_textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				lblNewLabel.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/Folder-icon.png")).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				lblNewLabel.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/Folder-icon.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
				
				FileDialog fd = new FileDialog(new JFrame(), "Choose a file", FileDialog.SAVE);
				fd.setDirectory("C:\\");
				fd.setVisible(true);
				String filename = fd.getDirectory();
				if (filename == null) {
					System.out.println("You cancelled the choice");
					return;
				}
				else
					System.out.println("You chose " + filename);
					filename = fd.getDirectory()+fd.getFile();
					textField.setText(filename);
			
			}
		});
		
		lblNewLabel.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/rasuta/images/Folder-icon.png")).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 4;
		gbc_lblNewLabel.gridy = 1;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Smestanje prekoracilaca", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 3;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 2;
		contentPane.add(panel, gbc_panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		group = new ButtonGroup();
		group2 = new ButtonGroup();
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("Sa fiksnim korakom k");
		rdbtnNewRadioButton.setSelected(true);
		rdbtnNewRadioButton.setActionCommand("0");
		panel.add(rdbtnNewRadioButton);
		
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("Sa zonom prekoracenja");
		panel.add(rdbtnNewRadioButton_1);
		rdbtnNewRadioButton_1.setActionCommand("1");
		
		group.add(rdbtnNewRadioButton);
		group.add(rdbtnNewRadioButton_1);
		
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Metoda transformacije", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.gridwidth = 3;
		gbc_panel_1.insets = new Insets(0, 0, 5, 5);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 3;
		contentPane.add(panel_1, gbc_panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
		JRadioButton rdbtnNewRadioButton_2 = new JRadioButton("Metoda ostatka pri deljenju");
		rdbtnNewRadioButton_2.setSelected(true);
		panel_1.add(rdbtnNewRadioButton_2);
		rdbtnNewRadioButton_2.setActionCommand("0");
		
		JRadioButton rdbtnNewRadioButton_3 = new JRadioButton("Metoda centralnih cifara kljuca");
		panel_1.add(rdbtnNewRadioButton_3);
		rdbtnNewRadioButton_3.setActionCommand("1");
		
		JRadioButton rdbtnNewRadioButton_4 = new JRadioButton("Metoda preklapanja");
		panel_1.add(rdbtnNewRadioButton_4);
		rdbtnNewRadioButton_4.setActionCommand("2");
		
		group2.add(rdbtnNewRadioButton_2);
		group2.add(rdbtnNewRadioButton_3);
		group2.add(rdbtnNewRadioButton_4);
		
		JLabel lblNewLabel_1 = new JLabel("Faktor baketiranja");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 4;
		contentPane.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		textField_1 = new JTextField();
		textField_1.setText("3");
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 5);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 2;
		gbc_textField_1.gridy = 4;
		contentPane.add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);
		
		chckbxNewCheckBox = new JCheckBox("Ucitaj datoteku prilikom kreiranja");
		GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
		gbc_chckbxNewCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox.gridx = 3;
		gbc_chckbxNewCheckBox.gridy = 4;
		contentPane.add(chckbxNewCheckBox, gbc_chckbxNewCheckBox);
		
		JLabel lblBrojBlokova = new JLabel("Broj blokova");
		GridBagConstraints gbc_lblBrojBlokova = new GridBagConstraints();
		gbc_lblBrojBlokova.anchor = GridBagConstraints.WEST;
		gbc_lblBrojBlokova.insets = new Insets(0, 0, 5, 5);
		gbc_lblBrojBlokova.gridx = 1;
		gbc_lblBrojBlokova.gridy = 5;
		contentPane.add(lblBrojBlokova, gbc_lblBrojBlokova);
		
		textField_2 = new JTextField();
		textField_2.setText("7");
		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.insets = new Insets(0, 0, 5, 5);
		gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_2.gridx = 2;
		gbc_textField_2.gridy = 5;
		contentPane.add(textField_2, gbc_textField_2);
		textField_2.setColumns(10);
		
		JButton btnOdustani = new JButton("Odustani");
		btnOdustani.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
				
			}
		});
		GridBagConstraints gbc_btnOdustani = new GridBagConstraints();
		gbc_btnOdustani.anchor = GridBagConstraints.WEST;
		gbc_btnOdustani.insets = new Insets(0, 0, 5, 5);
		gbc_btnOdustani.gridx = 1;
		gbc_btnOdustani.gridy = 7;
		contentPane.add(btnOdustani, gbc_btnOdustani);
		
		JButton btnNewButton = new JButton("Kreiraj datoteku");
		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
		
			rasuta = new RasutaSaPrekoraciocem(Integer.parseInt(textField_1.getText()), 
											   Integer.parseInt(textField_2.getText()),
											   Integer.parseInt(group2.getSelection().getActionCommand()));
			dispose();
			}
			
		});
		
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.anchor = GridBagConstraints.EAST;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 3;
		gbc_btnNewButton.gridy = 7;
		contentPane.add(btnNewButton, gbc_btnNewButton);
		
		
	}
	public File getFajl() {
		return new File(textField.getText());
	}

	public RasutaSaPrekoraciocem getRasuta() {
		return rasuta;
	}
}
