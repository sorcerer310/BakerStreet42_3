package com.bsu.bk42;

/**
 * 游戏进度
 * Created by FC on 2015/7/13.
 */
public class GamePropress {
    private int taskCount = 1;                                                                                       //任务数量
    private int completeCount = 0;                                                                                  //当前任务完成数量
    private float percent = .0f;                                                                                     //当前任务完成百分比
    private TaskCompletedListener listener = null;                                                                   //有任务完成时的监听器

    /**
     * 设置任务完成数量
     * @param tc    任务数量
     * @return      返回当前任务完成的百分比
     */
    public float setTaskCompleteCount(int tc){
        completeCount = tc;
        float p = (float)completeCount/(float)taskCount;
        if(listener!=null)
            listener.completed(completeCount,p);
        return p;
    }

    /**
     * 设置任务完成监听器
     * @param l     监听器对象
     */
    public void setTaskCompletedListener(TaskCompletedListener l){
        listener = l;
    }

    /**
     * 任务完成监听器
     */
    interface TaskCompletedListener{
        void completed(int completedCount,float percent);
    }
}
