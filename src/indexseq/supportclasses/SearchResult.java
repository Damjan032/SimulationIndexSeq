package indexseq.supportclasses;

import indexseq.blockandcomponents.Block;
import indexseq.blockandcomponents.BlockPointer;

public class SearchResult {
    public boolean found;
//    private Integer blockAddress;
    public BlockPointer pointer;
    public Integer parentAddress;
    public Integer recordAddress;
    public boolean active = true;
//    private boolean overflow;
//    private Integer pointer;

    //blok u kom je nadjen
    public Block foundBlock;


    public Integer leafRecordAddress;
    public Integer leafAddress;

    public SearchResult(boolean b) {
        this(b,null,null,null,null);
    }
    public SearchResult(boolean b, BlockPointer pointer) {
        this(b,pointer,-1,null,null);
    }
    public SearchResult(boolean b, BlockPointer pointer,Block block) {
        this(b,pointer,-1,block,-1);
    }

    public SearchResult(boolean found, BlockPointer address, int recordAddress, Block foundBlock) {
        this(found,address,recordAddress,foundBlock,null);
    }


    public SearchResult(boolean found, BlockPointer pointer, Block foundBlock, Integer parentAddress) {
        this(found,pointer,null,foundBlock,parentAddress);
    }

    public SearchResult(boolean found, BlockPointer pointer, Integer recordAddress, Block foundBlock, Integer parentAddress) {
        this.found = found;
        this.pointer = pointer;
        this.recordAddress = recordAddress;
        this.foundBlock = foundBlock;
        this.parentAddress = parentAddress;
    }

    public SearchResult(SearchResult s, Integer address) {
        this.found = s.found;
        this.pointer = s.pointer;
        this.recordAddress = s.recordAddress;
        this.foundBlock = s.foundBlock;
        this.parentAddress = address;
    }

    public SearchResult(boolean b, BlockPointer pointer, int addressNum) {
        this(b,pointer,-1,null,addressNum);

    }

    public SearchResult() {

    }


    @Override
    public String toString() {
        return "SearchResult{" +
                "found=" + found +
                ", pointer=" + pointer +
                ", recordAddress=" + recordAddress +
                ", parent_pointer=" + parentAddress +
                '}';
    }
}