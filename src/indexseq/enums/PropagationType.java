package indexseq.enums;

public enum PropagationType{
    HIGH(0),LOW(1);
    int order;
    PropagationType(int i) {
        order = i;
    }

    public int getOrder() {
        return order;
    }
}
