package indexseq.supportclasses;

import indexseq.blockandcomponents.Record;
import indexseq.blockandcomponents.BlockPointer;

public class AddResult {
    public Record record;
    public BlockPointer pointer;

    public AddResult(Record r, BlockPointer pointer) {
        this.record = r;
        this.pointer = pointer;
    }
}
