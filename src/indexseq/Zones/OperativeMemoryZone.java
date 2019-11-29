package indexseq.Zones;

import indexseq.animator.Animator;
import indexseq.blockandcomponents.Block;
import indexseq.enums.FileStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerListener;
import java.util.concurrent.Callable;

public class OperativeMemoryZone extends JPanel {


    private JPanel content;
    private PrimaryZone primaryZone;
    private OverflowZone overflowZone;
    private static OperativeMemoryZone instance;

    private OperativeMemoryZone(){
        setupLayout();
    }

    public static OperativeMemoryZone init(){
        if (instance==null){
            instance = new OperativeMemoryZone();
        }
        return instance;
    }

    private void setupLayout(){
        setLayout(new BorderLayout());
        add(new JLabel("Operativna memorija"), BorderLayout.NORTH);
        content = new JPanel();
        content.setLayout(new GridBagLayout());
        add(content, BorderLayout.CENTER);
    }

    public Block loadBlock(Block b){
        Block loadedBlock = new Block(b);
        loadedBlock.setFileStatus(FileStatus.LOADED);
        Animator.init().addAnimation(b.getLoadAnim());
        Animator.init().addCallback(new Callable() {
            @Override
            public Object call() throws Exception {
                addBlockToLayout(loadedBlock);
                return null;
            }
        });
        Animator.init().addAnimation(loadedBlock.getGrowAnim());
        return loadedBlock;
    }

    public void unloadLast(Block loadedBlock){
//        Animator.init().addLastAnim(loadedBlock.getShrinkAnim());

        Animator.init().addLastCallback(new Callable() {
            @Override
            public Object call() throws Exception {
                OperativeMemoryZone.init().removeBlockFromLayout(loadedBlock);
                return null;
            }
        });
        Animator.init().addLastCallback(new Callable() {
            @Override
            public Object call() throws Exception {
                updateBlock(loadedBlock);
                return null;
            }
        });
        Animator.init().addLastAnim(loadedBlock.getUnloadAnim());
    }

    public void unloadAndSaveBlock(Block loadedBlock) {
//        Animator.init().addAnimation(loadedBlock.getShrinkAnim());

        Animator.init().addCallback(new Callable() {
            @Override
            public Object call() throws Exception {
                OperativeMemoryZone.init().removeBlockFromLayout(loadedBlock);
                return null;
            }
        });
        Animator.init().addCallback(new Callable() {
            @Override
            public Object call() throws Exception {
                updateBlock(loadedBlock);
                return null;
            }
        });
        Animator.init().addAnimation(loadedBlock.getUnloadAnim());
    }
    public void unloadBlock(Block loadedBlock) {

        Animator.init().addAnimation(loadedBlock.getUnloadAnim());
        Animator.init().addCallback(new Callable() {
            @Override
            public Object call() throws Exception {
                OperativeMemoryZone.init().removeBlockFromLayout(loadedBlock);
                return null;
            }
        });
    }
    private void addBlockToLayout(Block b){
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5,5,5,5);
        content.add(b, gc);
        updateUI();
        repaint();
    }

    public void removeBlockFromLayout(Block loadedBlock){
        content.remove(loadedBlock);
        updateUI();
        repaint();
    }

    private void updateBlock(Block b){
        switch (b.getAddress().getType()){
            case PRIMARY: {
                primaryZone.setBlock(b,b.getAddressNum());
                break;
            }
            case OVERFLOW: {
                overflowZone.setBlock(b,b.getAddressNum());
                break;
            }
        }
    }

    public void setPrimaryZone(PrimaryZone primaryZone) {
        this.primaryZone = primaryZone;
    }

    public void setOverflowZone(OverflowZone overflowZone) {
        this.overflowZone = overflowZone;
    }

    public void clear() {
        content.removeAll();
    }
    @Override
    public synchronized void addContainerListener(ContainerListener l) {
        super.addContainerListener(l);
        content.addContainerListener(l);
    }
}
