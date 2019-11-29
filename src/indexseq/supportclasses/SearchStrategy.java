package indexseq.supportclasses;

import indexseq.blockandcomponents.NodeBlock;

import java.io.Serializable;

public interface SearchStrategy extends Serializable {
    SearchResult findNext(NodeBlock b, int key);
}
