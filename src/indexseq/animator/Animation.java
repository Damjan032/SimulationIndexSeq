package indexseq.animator;

import javax.swing.*;
import java.awt.event.ActionListener;

public class Animation {
    //    HorizontalFocuser animationListener;
    private Animator animator = null;
    private JComponent animatedObject;
    private ActionListener actionListener;
    private int step = 0;
    //TODO odrediti najbolju kandidati za sad:10,15,20
    private int stepNum = 20;
    //    int initDelay;
    private Timer animationTimer = new Timer(0, null);


    public Animation() {
    }

    public Animation(JComponent animatedObject) {
        this.animatedObject = animatedObject;
    }


    //    public Animation(int step, int stepNum) {
//        this.step = step;
//        this.stepNum = stepNum;
//    }
    //        this.delay = delay;
//        this.initDelay = initDelay

    public void addActionListener(ActionListener a) {
        actionListener = a;
        animationTimer.addActionListener(actionListener);

    }

    void run(int time) {
        step = 0;
        setDelay(time);
        animationTimer.start();
    }

    public void run() {
        run( Animator.init().getProcessDelay());
    }

    public void kill() {
        if (animatedObject != null) {
            animatedObject.repaint();
        }
        animationTimer.stop();
    }

    public void end() {
//        setDelay(1);
        kill();
        if (animator != null) {
            animator.autoNext();
        }
    }

    public void pause() {
        animationTimer.stop();
    }

    public void start() {
        animationTimer.start();
    }

    public void setDelay(int time) {
        int delay = (int) Math.ceil((float) time / stepNum);
        boolean running = animationTimer.isRunning();
        if (running) {
            animationTimer.stop();
        }
        animationTimer.setDelay(delay);
        if (running) {
            animationTimer.start();
        }
    }

    public int getStep() {
        return step;
    }

    public int getStepNum() {
        return stepNum;
    }

//    public void setAnimator(Animator blockAnimator) {
//        this.blockAnimator = blockAnimator;
//    }

    public void incStep() {
        ++step;
    }

    public void setAnimator(Animator animator) {
        this.animator = animator;
    }

    public void wrapItup() {
        setDelay(1);
        if (!animationTimer.isRunning()){
            run();
        }
    }

    boolean isDone(){
        return !animationTimer.isRunning();
    }

    public boolean isRunning() {
        return animationTimer.isRunning();
    }
}
