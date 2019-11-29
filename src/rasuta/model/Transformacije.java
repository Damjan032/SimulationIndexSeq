package rasuta.model;

public class Transformacije {
	
	public static int metodaOstatkaPriDeljenju(int kljuc, int B) {
		return 1+kljuc%B;
	}
	
	public static int metodaKvadratSrednjih(int kljuc, int brCifara, int B) {
		//brCifara - broj duzine kljuca, npra 00123 brCifara=5
		int n = (int)Math.ceil(Math.log10(B));
		int t = (int)Math.floor(brCifara - n/2);
		kljuc *= kljuc;
		int r = (int)Math.floor(kljuc/Math.pow(10, t));
		r = r%(int)Math.pow(10, n);
		r = (int)Math.floor(r*B/Math.pow(10, n)) + 1;
		return r;
	}
	
	public static int metodPreklapanja(int kljuc, int brCifara, int B) {
		//brCifara - broj duzine kljuca, npra 00123 brCifara=5
		int zbir = 0;
		int n = (int)Math.ceil(Math.log10(B));
		int maxiter = brCifara/n;
		for(int i = 0; i < maxiter; ++i) {
			if(i%2 == 0)
				zbir += odvojiCifre(n*i+1, n*(i+1), kljuc);
			else {
				for(int j = 0; j < n; ++j) {
					zbir += odvojiCifre(n*i+1+j, n*i+1+j, kljuc)*(int)Math.pow(10, n-j-1);
				}
			}
		}
		
		return (int)Math.floor(zbir%(int)Math.pow(10, n) * B/Math.pow(10, n)) + 1;
	}
	
	private static int odvojiCifre(int pocetak, int kralj, int br) {
		return (int) Math.floor((br%(int)Math.pow(10, kralj)) / Math.pow(10, pocetak - 1));
	}
}
