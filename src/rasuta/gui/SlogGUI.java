package rasuta.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import rasuta.model.Slog;

public class SlogGUI extends JPanel {
	
	public Slog slog;
	private JLabel lblSifra;
	private JLabel label_1;
	private JLabel lblNewLabel;
	private String text;
	
	
	/**
	 * Create the panel.
	 */
	public SlogGUI() {
		setBorder(new TitledBorder(null, "Slog1", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		setLayout(new GridLayout(4, 1, 2, 2));
		lblSifra = new JLabel("Sifra:");
		add(lblSifra);
		
		label_1 = new JLabel("Broj celije:");
		add(label_1);
		
		lblNewLabel = new JLabel("Duzina kazne:");
		add(lblNewLabel);

	}
	
	public void addSlog(Slog s) {
		slog = s;
		text = "Sifa: " + s.getZatvorenik().getSifra();
		lblSifra.setText(text);
		text = "Broj celije: " + s.getZatvorenik().getBrojCelije();
		label_1.setText(text);
		text = "Duzina kazne: " + s.getZatvorenik().getDuzinaKazne();
		lblNewLabel.setText(text);
	}

	public void paintGreen() {
		 
		Border border = BorderFactory.createLineBorder(Color.GREEN);
		setBorder(border);	
	
		
	}
	public void paintRed() {
		 
		Border border = BorderFactory.createLineBorder(Color.RED);
		setBorder(border);
		     
	}
	
	public void resetBorder() {
		 	  
		setBorder(new TitledBorder(null, "Slog1", TitledBorder.CENTER, TitledBorder.TOP, null, null));	
		
	}
}
