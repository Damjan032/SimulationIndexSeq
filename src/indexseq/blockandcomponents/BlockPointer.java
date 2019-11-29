package indexseq.blockandcomponents;

import indexseq.enums.PointerType;

import java.io.Serializable;

public class BlockPointer implements Serializable {


    private String name;
    private PointerType type;
    private Integer address;

    public BlockPointer(PointerType type, Integer address) {
        this.type = type;
        this.address = address;
    }

    public BlockPointer(String name) {
        this.type = PointerType.SPECIAL;
        this.name = name;
        address = -1;
    }

    public BlockPointer(BlockPointer value) {
        type = value.type;
        name = value.name;
        address = value.address;
    }

    public PointerType getType() {
        return type;
    }

    public Integer getAddress() {
        return address;
    }

    @Override
    public String toString() {
        switch (type) {
            case TREE:
                return "S" + address;
            case PRIMARY:
                return "P" + address;
            case OVERFLOW:
                return "Z" + address;
            default:
                return name;
        }
    }
}
