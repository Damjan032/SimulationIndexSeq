package indexseq.enums;

public enum FileStatus{
    LOADED(false),UNLOADED(true);
    private boolean status;
    FileStatus(boolean b) {
        status = b;
    }
    public boolean isStatus(){
        return status;
    }
}
