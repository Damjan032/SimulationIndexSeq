package indexseq;

import indexseq.Zones.PrimaryZone;
import indexseq.blockandcomponents.Block;
import indexseq.blockandcomponents.NodeBlock;
import indexseq.blockandcomponents.Record;
import indexseq.enums.OverFlowType;
import indexseq.enums.PointerType;
import indexseq.blockandcomponents.BlockPointer;
import indexseq.enums.PropagationType;

import java.util.*;

import static indexseq.enums.PropagationType.HIGH;
import static indexseq.enums.PropagationType.LOW;


public class Main {
    public static void main(String[] args){
      //  main33();
//       main11();
    realMain();
    }

    public static void realMain(){
        MainFrame.init();
    }

    public static void main11() {
        Random r = new Random();

        ArrayList<Block> blocks = new ArrayList<>();
        ArrayList<Record> records;
        int order = 3;
        int blockFactor = 3;

        int blockNum = 13;
        int recordNum = blockNum*blockFactor ;
        int fieldNum = 1;

        ArrayList<Integer> values = new ArrayList<>();
        Set<Integer> valueSet = new TreeSet<>();
        for (int i = 0;i<recordNum;i++){
            int n;
            do{
                n = r.nextInt(99);
            }while (valueSet.contains(n));
            valueSet.add(n);
            values.add(n);
        }
        values.sort(null);
        for(int i = 0;i<recordNum;i+=blockFactor){
            records = new ArrayList<>();
            for (int j = 0; j <blockFactor; j++) {
                records.add(new Record(values.get(i+j), new ArrayList<>(Collections.singletonList("Polje"))));
            }
            Block b = new Block(new BlockPointer(PointerType.PRIMARY,i/blockFactor),blockFactor,fieldNum, records);
            blocks.add(b);
        }


        PrimaryZone primaryZone = new PrimaryZone(blocks,blockFactor);
        primaryZone.setFieldNum(fieldNum);
        IndexSequential is = new DirectIndexSeq("",order, primaryZone, HIGH);
        MainFrame.init("Proba", is);

    }

    public static void main33() {

    	ArrayList<Block> blocks = new ArrayList<>();
        ArrayList<Record> records;
        int order = 2;
        int blockFactor = 3;
        int fieldNum = 1;


//        ArrayList<NodeBlock> leafs = new ArrayList<>();
        ArrayList<Block> children = new ArrayList<>();
        NodeBlock nb;

//        int[][] recordkeys = new int[][]{new int[]{3, 7, 13}, new int[]{15, 19, 23}, new int[]{25, 27, 29}, new int[]{34, 43, 49}, new int[]{64}};
        int[][] recordkeys = new int[][]{new int[]{1, 3, 6}, new int[]{8, 12, 16}, new int[]{18, 24, 29},
                new int[]{35, 38, 39}, new int[]{45, 46,48}, new int[]{55, 57, 59}, new int[]{61, 66, 67},
                new int[]{71, 73, 78}, new int[]{81, 82, 84}, new int[]{85, 88, 90}, new int[]{91, 92, 93},
                new int[]{94, 95, 96}, new int[]{97}};
        for (int n = 0; n < recordkeys.length; n++) {
            records = new ArrayList<>();
            for (int i = 0; i < recordkeys[n].length; i++) {
                records.add(new Record(recordkeys[n][i], new ArrayList<>(Collections.singletonList("Polje"))));
            }
            Block b = new Block(new BlockPointer(PointerType.PRIMARY,n), blockFactor,fieldNum, records);
            children.add(b);
            if ((n + 1) % order == 0 || (n + 1) == recordkeys.length) {
//                leafs.addRecord(new NodeBlock(n/order+1,order,true,children));
                children = new ArrayList<>();
            }
            blocks.add(b);
        }
        PrimaryZone primaryZone = new PrimaryZone(blocks,blockFactor);
        primaryZone.setFieldNum(fieldNum);
        primaryZone.setOverFlowType(OverFlowType.INDIRECT);
//        TreePane tp = new TreePane(order,p);
//        TreePane tp = new TreePane(order);
//        tp.addLeafs(leafs);


//        int[] keys = new int[]{13, 23, 29, 49, 60, 99};
//        ArrayList<NodeBlock> leafs = new ArrayList<>();
//        for (int width = 0; width < 3; width++) {
//            ArrayList<Block> children = new ArrayList<>();
//            for (int height = 0; height < 2; height++) {
//
//                children.addRecord(new Block((width * 2 + height, 2));
//            }
//            leafs.addRecord(new NodeBlock(width + 1, 2, children));
//        }


//        CentralView cv = new CentralView(p);
//        MainFrame f = new MainFrame("Proba", size, p, tp);
        IndexSequential is = new IndirectIndexSeq("",3, primaryZone, HIGH);
        MainFrame.init("Proba", is);

       
    }

//    public static void main(String[] args) {
//        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//
//        ArrayList<Block> blocks = new ArrayList<>();
//        ArrayList<Record> records;
//        int order = 2;
//        int blockFactor = 3;
//
//
//        ArrayList<NodeBlock> leafs = new ArrayList<>();
//        ArrayList<Block> children = new ArrayList<>();
//        NodeBlock nb;
//
//        int[][] recordkeys = new int[][]{new int[]{3, 7, 13}, new int[]{15, 19, 23}, new int[]{25, 27, 29}, new int[]{34, 43, 49}, new int[]{64}};
//        for (int n = 0; n < recordkeys.length; n++) {
//            records = new ArrayList<>();
//            for (int i = 0; i < recordkeys[n].length; i++) {
//                records.addRecord(new Record(recordkeys[n][i], new String[]{"Polje"}));
//            }
//            Block b = new Block(n, blockFactor, records, BoxLayout.Y_AXIS);
//            children.addRecord(b);
//            if((n+1)%order==0 || (n+1)==recordkeys.length){
//                leafs.addRecord(new NodeBlock(n/order+1,order,true,children));
//                children = new ArrayList<>();
//            }
//            blocks.addRecord(b);
//        }
//        PrimaryZone p = new PrimaryZone(blocks);
//        TreePane tp = new TreePane(2);
//        tp.addLeafs(leafs);
////        int[] keys = new int[]{13, 23, 29, 49, 60, 99};
////        ArrayList<NodeBlock> leafs = new ArrayList<>();
////        for (int width = 0; width < 3; width++) {
////            ArrayList<Block> children = new ArrayList<>();
////            for (int height = 0; height < 2; height++) {
////
////                children.addRecord(new Block((width * 2 + height, 2));
////            }
////            leafs.addRecord(new NodeBlock(width + 1, 2, children));
////        }
//
//
//
////        CentralView cv = new CentralView(p);
//        Dimension size = new Dimension(2 * d.width / 3, 2 * d.height / 3);
////        MainFrame f = new MainFrame("Proba", size, p, tp);
////        f.setLocation((d.width - size.width) / 2, (d.height - size.height) / 2);
////        f.setVisible(true);
//    }
}
