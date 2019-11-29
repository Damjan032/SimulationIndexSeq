package rasuta.model;

import java.io.Serializable;
import java.util.ArrayList;

public class RasutaSaSerijskom extends Rasuta implements Serializable{
	
	private ArrayList<Baket> zonaPrekoracenja;
	
	public RasutaSaSerijskom(int faktor_baketiranja, int brojBaketa, int transformacija) {
		super(faktor_baketiranja, brojBaketa, transformacija);
		this.primarnaZona = new ArrayList<Baket>(brojBaketa);
		this.zonaPrekoracenja = new ArrayList<Baket>(brojBaketa);
	}
	
	public void formiraj() {
		for (int i = 0; i < brojBaketa; i++) {
			Baket baket = new Baket();
			for (int j = 0; j < faktor_baketiranja; j++) {
				Slog slog = new Slog(new Zatvorenik(0, 0, 0),2);
				baket.getSlogovi().add(slog);	
			}
			baket.setAdresa(i+1);
			primarnaZona.add(baket);
			zonaPrekoracenja.add(baket);
		}
	}
}
