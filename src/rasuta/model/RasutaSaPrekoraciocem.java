package rasuta.model;

import java.io.Serializable;
import java.util.ArrayList;

public class RasutaSaPrekoraciocem extends Rasuta implements Serializable{
	
	public RasutaSaPrekoraciocem(int faktor_baketiranja, int brojBaketa, int transformacija) {
		super(faktor_baketiranja, brojBaketa, transformacija);
		this.primarnaZona = new ArrayList<Baket>(brojBaketa);
		formiraj();
	}
	
	public void formiraj() {
		for (int i = 0; i < brojBaketa; i++) {
			Baket baket = new Baket();
			for (int j = 0; j < faktor_baketiranja; j++) {
				Slog slog = new Slog(new Zatvorenik(0, 0, 0), 2);
				baket.getSlogovi().add(slog);	
			}
			baket.setAdresa(i+1);
			primarnaZona.add(baket);
		}
	}
}
