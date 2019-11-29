package indexseq.enums;

public enum OverFlowType{
    DIRECT,INDIRECT;


    @Override
    public String toString() {
        if (this==DIRECT){
            return "Direktno";
        }
        return "Indirektno";
    }
}
