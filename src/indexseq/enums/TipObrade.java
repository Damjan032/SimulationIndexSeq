package indexseq.enums;

public enum TipObrade {
	DIREKTNA(0), REDOSLDEDNA(1);
	int order;
	TipObrade (int i) {
        order = i;
    }

    public int getOrder() {
        return order;
    }

}
