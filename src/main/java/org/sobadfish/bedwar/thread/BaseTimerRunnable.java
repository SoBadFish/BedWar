package org.sobadfish.bedwar.thread;

import org.sobadfish.bedwar.manager.ThreadManager;

/**
 * @author SoBadFish
 * 2022/1/7
 */
public abstract class BaseTimerRunnable extends ThreadManager.AbstractBedWarRunnable{

    private int time = 0;

    private final int end;

    private boolean close;

    public BaseTimerRunnable(int end){
        this.end = end;
    }

    public void cancel(){
        this.close = true;
    }

    @Override
    public void run() {
        if(close){
            isClose = true;
            return;
        }
        if(time < end){
            time++;
            onRun();
        }else{
            callback();
            isClose = true;

        }

    }

    public void onRun(){}

    /**
     * 当倒计时结束的回调
     * */
    abstract protected void callback();
}
