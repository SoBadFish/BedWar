package org.sobadfish.bedwar.thread;

/**
 * @author SoBadFish
 * 2022/1/7
 */
public abstract class BaseTimerRunnable implements Runnable{
    private int time = 0;
    private int end;
    public BaseTimerRunnable(int end){
        this.end = end;
    }

    @Override
    public void run() {
        while (true){
            if(time < end){
                time++;
                onRun();
            }else{
                callback();
                return;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onRun(){}

    /**
     * 当倒计时结束的回调
     * */
    abstract protected void callback();
}
