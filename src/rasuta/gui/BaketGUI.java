package rasuta.gui;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import rasuta.model.Baket;

public class BaketGUI extends JPanel {

	public ArrayList<SlogGUI> slogoviGui = new ArrayList<>();
	/**
	 * Create the panel.
	 * 
	 */
	private int adresa; 	//pokusaj da se resi problem prenosenja vrednosti u SwingWorker
	
	public BaketGUI() {
		setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Blok1", TitledBorder.CENTER, TitledBorder.TOP, null, Color.RED));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

	}

	public void addBaket(Baket b) {

		for (int i = 0; i < b.getSlogovi().size(); i++) {
			SlogGUI s = new SlogGUI();
			s.addSlog(b.getSlogovi().get(i));
			String nazivSloga = "Slog"+String.valueOf(i+1);
			s.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), nazivSloga, TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
			slogoviGui.add(s);
			add(s);
		}
	}

	public int getAdresa() {
		return adresa;
	}

	public void setAdresa(int adresa) {
		this.adresa = adresa;
		setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Baket"+String.valueOf(adresa), TitledBorder.CENTER, TitledBorder.TOP, null, Color.RED));
	}
	
	
	
}
