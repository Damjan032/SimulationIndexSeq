package indexseq.animator;

import indexseq.MainFrame;
import indexseq.UndoSystem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

public class Animator {
    private static Animator instance = null;


    //    private ArrayList<Animation> repeating = new ArrayList<>();
    private ArrayList<Animation> animations = new ArrayList<>();
    private Animation curentAnimation;
    private int index;
    private int processDelay = 100;

    //after runing animations call this
    private Callable<Object> initCallback;


    //functions to run after some animations
    private HashMap<Integer, ArrayList<Callable>> callables = new HashMap<>();

    private List<Animation> lastAnimations = new ArrayList<>();
    private List<Callable> lastCallbacks = new ArrayList<>();

    //listeners to run while animating
    private ArrayList<ActionListener> runningListeners;
    //listeners to after animating
    private ArrayList<ActionListener> afterAnimListeners;

//    private Timer processTimer = new Timer(processDelay, new ProcessListener());
    private boolean automatic = true;
    private int oldDelay;


    private Animator() {

    }

    static public Animator init() {
        if (instance == null) {
            instance = new Animator();
        }
        return instance;
    }

    public void setInitCallback(Callable<Object> callback) {
        instance.initCallback = callback;
    }

    public void setProcessDelay(int s) {
//        processDelay = (int) (1000 - 1000 * (float)s / MainFrame.MAX_VAL);
        processDelay =s;
        oldDelay = processDelay;
        if (curentAnimation != null) {
            curentAnimation.setDelay(processDelay);
        }
//        processTimer.setDelay(processDelay);
    }

    public void processAnimations() {
        clear();
        UndoSystem.init().reset();
        processDelay = oldDelay;
        runAnimListeners();
        index =0;
        
        try {
            initCallback.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (instance.automatic) {
//            processTimer.start();
            instance.next();
        }
    }

    private void next() {
            runOne();
    }

    private void runOne(){
        ArrayList<Callable> currCallables = callables.get(index);
        if (currCallables !=null) {
            for (Callable c :
                    currCallables) {
                try {
                    c.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (index < animations.size()) {
            curentAnimation = animations.get(index);
            curentAnimation.run(processDelay);
            index++;
        }else{
            runLastAnimations();
            runAfterAnimListeners();
            curentAnimation = null;
        }
    }


    public void autoNext() {
        if (automatic) {
            next();
        }
    }

    public void manualNext() {
        if (!automatic&&index<animations.size()) {
            next();
        }
    }

//    public void run() {
//        processTimer.start();
//    }

    public void pause() {
        if (curentAnimation != null) {
            curentAnimation.pause();
        }
    }

    public void stop() {
        index = 0;
        if (curentAnimation != null) {
            curentAnimation.kill();
        }
        runAfterAnimListeners();
        //Posto se ponovo sve radi!!!
    }

    public void end(){
        if (curentAnimation!=null) {
            oldDelay = processDelay;
            processDelay = 0;
            if (!curentAnimation.isRunning()) {
                curentAnimation.run();
            }
            curentAnimation.wrapItup();
//            index++;
        }
    }

//    public void runLeftOver(){
//        int oldDelay = processDelay;
//        for (int i = index; i <= animations.size(); ++i) {
//            runOne();
//        }
//        runLastAnimations();
//        runAfterAnimListeners();
//        index = 0;
//        processDelay = oldDelay;
//    }


    public void start() {
        if (curentAnimation != null) {
            curentAnimation.start();
        }
    }

    public void restart() {
        stop();
//        animations = repeating;
        UndoSystem.init().undo();
        processAnimations();
    }

    public void clear() {
        instance.animations.clear();
        callables.clear();
        lastAnimations.clear();
        lastCallbacks.clear();
    }

    public void addAnimation(Animation a) {
        a.setAnimator(instance);
        instance.animations.add(a);
    }

    public void addAnimations(ArrayList<Animation> anim) {
        for (Animation a : anim) {
            addAnimation(a);
        }
    }
    public void addCallback(Callable c){
        ArrayList<Callable> calls = callables.get(animations.size());
        if (calls==null){
            calls = new ArrayList<>();
        }
        calls.add(c);
        callables.put(animations.size(),calls);
    }

    public void addLastAnim(Animation a) {
        lastAnimations.add(a);
    }
    public void addLastCallback(Callable c){
        lastCallbacks.add(c);
    }
    public void addRuningListener(ActionListener a){
        if (runningListeners ==null){
            runningListeners = new ArrayList<>();
        }
        runningListeners.add(a);
    }
    public void addAfterRuningListener(ActionListener a){
        if (afterAnimListeners==null){
            afterAnimListeners = new ArrayList<>();
        }
        afterAnimListeners.add(a);
    };

    public void setAnimations(ArrayList<Animation> anim) {
        animations.clear();
        addAnimations(anim);
    }

    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }

    public int getProcessDelay() {
        return processDelay;
    }

    public void freeze(){
        if (curentAnimation!=null) {
            curentAnimation.pause();
        }
        Timer t = new Timer(processDelay,null);
        t.addActionListener(e-> {t.stop();
            if (curentAnimation!=null) {
                curentAnimation.start();
            }});
        t.start();
    }
    private void runAnimListeners(){
        for (ActionListener a: runningListeners){
            a.actionPerformed(new ActionEvent(this,0,""));
        }
    }
    private void runAfterAnimListeners(){
        for (ActionListener a:afterAnimListeners){
            a.actionPerformed(new ActionEvent(this,0,""));
        }
    }

    private void runLastAnimations() {
        for (Animation a : lastAnimations){
            try {
                a.run(processDelay);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(MainFrame.init(),e.getMessage());
            }
        }
        for (Callable c:lastCallbacks){
            try {
                c.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}

