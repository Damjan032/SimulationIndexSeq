package indexseq.enums;
/*
Enum kojim se opisuju faze razvoja indeks sekvecijalne datoteke
    PRIMARY -> samo primarna zona
    OVERFLOW -> primarna sa zonom prekoracenja
    TREE -> krajnja faza u kojoj je formirao stablo

 */
public enum IndexFileStage {
    PRIMARY, OVERFLOW, TREE
}
