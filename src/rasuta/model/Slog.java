package rasuta.model;

import java.io.Serializable;

public class Slog implements Serializable{
	
	private Zatvorenik zatvorenik;
	private int status = 1; // 0 - aktivan, 1 - logicki obrisan, 2 - prazan
	
	public Slog(Zatvorenik zatvorenik) {
		this(zatvorenik, 0);
	}
	
	public Slog(Zatvorenik zatvorenik, int status) {
		super();
		this.setZatvorenik(zatvorenik);
		this.status = status;
	}

	public Zatvorenik getZatvorenik() {
		return zatvorenik;
	}

	public void setZatvorenik(Zatvorenik zatvorenik) {
		this.zatvorenik = zatvorenik;
	}

	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
