package rasuta.model;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Rasuta implements Serializable {
	
	protected int faktor_baketiranja;
	protected int brojBaketa;
	protected int transformacija; //0 - ostatak pri deljenju, 1 - centralne cifre kljuca, 2 - preklapanje
	protected ArrayList<Baket> primarnaZona;
	
	public Rasuta(int faktor_baketiranja, int brojBaketa, int transformacija) {
		super();
		this.primarnaZona = new ArrayList<Baket>(brojBaketa);
		this.faktor_baketiranja = faktor_baketiranja;
		this.brojBaketa = brojBaketa;
		this.transformacija = transformacija;
	}
	
	public abstract void formiraj();
	
	public int getFaktor_baketiranja() {
		return faktor_baketiranja;
	}

	public void setFaktor_baketiranja(int faktor_baketiranja) {
		this.faktor_baketiranja = faktor_baketiranja;
	}

	public int getBrojBaketa() {
		return brojBaketa;
	}

	public void setBrojBaketa(int brojBaketa) {
		this.brojBaketa = brojBaketa;
	}

	public int getTransformacija() {
		return transformacija;
	}

	public void setTransformacija(int transformacija) {
		this.transformacija = transformacija;
	}

	public ArrayList<Baket> getPrimarnaZona() {
		return primarnaZona;
	}

	public void setPrimarnaZona(ArrayList<Baket> primarnaZona) {
		this.primarnaZona = primarnaZona;
	}
	
	public void print() {
		
		for (int i = 0; i < getBrojBaketa(); i++) {
			System.out.println("\nBaket " + String.valueOf(i+1));
			for (int j = 0; j < getFaktor_baketiranja(); j++) {
				System.out.println("\n\nSlog "+ String.valueOf(j+1));
				primarnaZona.get(i).getSlogovi().get(j).getZatvorenik().toString();
			}
		}
	}
}
