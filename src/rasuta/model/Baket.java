package rasuta.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Baket implements Serializable {

	private ArrayList<Slog> slogovi;
	private int adresa;
	
	public Baket() {
		slogovi = new ArrayList<Slog>();
	}
	
	public Baket(int adresa) {
		slogovi = new ArrayList<Slog>();
		this.adresa = adresa;
	}
	
	public ArrayList<Slog> getSlogovi() {
		return slogovi;
	}

	public void setSlogovi(ArrayList<Slog> slogovi) {
		this.slogovi = slogovi;
	}

	public int getAdresa() {
		return adresa;
	}
	
	public void setAdresa(int adresa) {
		this.adresa = adresa;
	}
}
