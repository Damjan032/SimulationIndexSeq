package rasuta.model;

import java.io.Serializable;

public class Zatvorenik implements Serializable {

	public Zatvorenik(int sifra, int brojCelije, int duzinaKazne) {
		super();
		this.sifra = sifra;
		this.brojCelije = brojCelije;
		this.duzinaKazne = duzinaKazne;
	}
	private int sifra;
	private int brojCelije;
	private int duzinaKazne;
	
	public int getSifra() {
		return sifra;
	}
	public void setSifra(int sifra) {
		this.sifra = sifra;
	}
	public int getBrojCelije() {
		return brojCelije;
	}
	public void setBrojCelije(int brojCelije) {
		this.brojCelije = brojCelije;
	}
	public int getDuzinaKazne() {
		return duzinaKazne;
	}
	public void setDuzinaKazne(int duzinaKazne) {
		this.duzinaKazne = duzinaKazne;
	}
	@Override
	public String toString() {
		System.out.println("\n\n\nSifa: "+String.valueOf(getSifra()));
		System.out.println("\n\n\nBroj celije: "+String.valueOf(getBrojCelije()));
		System.out.println("\n\n\nDuzina kazen: "+String.valueOf(getDuzinaKazne()));
		return super.toString();
	}
}
