package indexseq.supportclasses;

import indexseq.blockandcomponents.NodeBlock;
import indexseq.blockandcomponents.Record;
import indexseq.animator.Animator;
import indexseq.blockandcomponents.BlockPointer;
import indexseq.enums.PropagationType;

import java.util.ArrayList;

class HighSearchStrategy implements SearchStrategy {

    @Override
    public SearchResult findNext(NodeBlock b, int key) {
        ArrayList<Record> records = b.getRecords();
        Animator.init().addAnimation(b.getHighlightAnim(false));
        SearchResult sr;
        for (int i = 0; i < b.getFilled(); ++i) {
            Record r = records.get(i);
            int index = 0;
            for (Integer recordKey :
                    r.getKeys()) {
                if (key <= recordKey) {
                    Animator.init().addAnimation(r.getSelectAnim());
                    BlockPointer pointer = r.getPointer(index);
                    sr = new SearchResult(true, pointer);
                    sr.leafRecordAddress = i;
                    sr.leafAddress = b.getAddressNum();
                    if (b.isLeaf()) {
                        sr.parentAddress = b.getAddressNum();
                        // System.out.println("VIDI OOOVOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO " + b.getAddressNum());
                        return sr;
                    }
                    return sr;
                }
                ++index;
            }

            //Pravljenje animacije farbanja sloga
            Animator.init().addAnimation(r.getWrongAnim());
        }
        return new SearchResult(false);
    }
}

class LowSearchStrategy implements SearchStrategy {


    @Override
    public SearchResult findNext(NodeBlock b, int key) {
        ArrayList<Record> records = b.getRecords();
        Animator.init().addAnimation(b.getHighlightAnim(false));
        SearchResult sr;

        BlockPointer lastPointer = null;
        for (int i = 0; i < b.getFilled(); ++i) {
            Record r = records.get(i);
            int index = 0;
            ArrayList<Integer> keys = r.getKeys();
            for (Integer recordKey : keys) {
                if (key >= recordKey) {

                    if (b.isLeaf()  &&index==0&&keys.size()>1 &&r.getKey(index+1) <= key) {
                        Animator.init().addAnimation(r.getSelectAnim());
                        index++;
                        continue;
                    }
                    int pom = i + 1;
                    if (pom >= b.getFilled()) {
                        lastPointer = r.getPointer(index);
                    } else {
                        Record r1 = records.get(pom);
                        if (r1.getKey() > key) {
                            lastPointer = r.getPointer(index);
                        } else {
                            continue;
                        }
                    }
                    Animator.init().addAnimation(r.getSelectAnim());
                    sr = new SearchResult(true, lastPointer);
                    sr.leafRecordAddress = i;
                    if (b.isLeaf()) {
                        sr.leafAddress = b.getAddressNum();
                        sr.parentAddress = b.getAddressNum();
                        // System.out.println("VIDI OOOVOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO " + b.getAddressNum());
                        return sr;
                    }
                    return sr;
                }
                ++index;
                Animator.init().addAnimation(r.getWrongAnim());
            }

        }
        sr = new SearchResult(false,lastPointer);
        sr.leafAddress = b.getAddressNum();
        return sr;
    }
}

public class SearchStrategyFactory {
    public static SearchStrategy makeSearchStrategy(PropagationType propagationType) {
        switch (propagationType) {

            case HIGH: {
                return new HighSearchStrategy();
            }
            case LOW: {
                return new LowSearchStrategy();
            }
            default: {
                return null;
            }
        }
    }
}
