package com.wisdomrouter.app.tools;


import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2015/12/15.
 */
public class TaskHandler<T> extends Handler {
    WeakReference<T> weakReference;

    public TaskHandler(T object) {
        weakReference = new WeakReference<>(object);
    }

    /**
     * 任务执行成功
     */
    public static final int TASK_OK = 0x10000001;
    /**
     * 任务执行
     */
    public static final int TASK_ON = 0x10000004;
    /**
     * 任务执行失败
     */
    public static final int TASK_FAILED = 0x10000002;
    /**
     * 任务取消执行
     */
    public static final int TASK_CANCELED = 0x10000003;

    /**
     * 消息接受处理
     */
    @Override
    public void handleMessage(Message msg) {
        T object = weakReference.get();
        if (object != null) {
            switch (msg.what) {
                case TASK_OK:
                    onTaskOk(object, msg);
                    break;
                case TASK_FAILED:
                    onTaskFailed(object, msg);
                    break;
                case TASK_CANCELED:
                    onTaskCanceled(object, msg);
                    break;
                case TASK_ON:
                    onTaskOk(object, msg);
                    break;

            }
        }
    }


    /**
     * 发送带消息的失败消息
     */
    public void sendFailedMessage(Object object) {
        Message msg = Message.obtain();
        msg.what = TASK_FAILED;
        msg.arg1 = 0;
        if (null != object) {
            msg.obj = object;
        }
        this.sendMessage(msg);
    }

    /**
     * 发送带消息的失败消息
     */
    public void sendFailedMessage(Object object, int arg1) {
        Message msg = Message.obtain();
        msg.what = TASK_FAILED;
        msg.arg1 = arg1;
        if (null != object) {
            msg.obj = object;
        }
        this.sendMessage(msg);
    }
    /**
     * 发送带消息的另起线程
     */
    public void sendOnMessage(Object object, int arg1) {
        Message msg = Message.obtain();
        msg.what = TASK_ON;
        msg.arg1 = arg1;
        if (null != object) {
            msg.obj = object;
        }
        this.sendMessage(msg);
    }

    /**
     * 发送带延时消息的失败消息
     */
    public void sendFailedDelayedMessage(Object object, int arg1,
                                         long timedelay) {
        Message msg = Message.obtain();
        msg.what = TASK_FAILED;
        msg.arg1 = arg1;
        if (null != object) {
            msg.obj = object;
        }
        this.sendMessageDelayed(msg, timedelay);
    }

    public void sendEmptyFailedMessage() {
        this.sendEmptyMessage(TASK_FAILED);
    }

    // 发送带消息的成功消息
    public void sendSucessMessage(Object object) {
        Message msg = Message.obtain();
        msg.what = TASK_OK;
        msg.arg1 = 0;
        if (null != object) {
            msg.obj = object;
        }
        this.sendMessage(msg);
    }

    // 发送带消息的成功消息
    public void sendSucessMessage(Object object, int arg1) {
        Message msg = Message.obtain();
        msg.what = TASK_OK;
        msg.arg1 = arg1;
        if (null != object) {
            msg.obj = object;
        }
        this.sendMessage(msg);
    }

    public void sendEmptySucessMessage() {
        this.sendEmptyMessage(TASK_OK);
    }

    public void sendEmptyTaskCancelMessage() {
        this.sendEmptyMessage(TASK_CANCELED);
    }

    /**
     * 当消息类型为TASK_OK时，回调该方法 可以通过arg1进行消息标记
     */
    public void onTaskOk(T object, Message msg) {

    }
    /**
     * 当消息类型为TASK_ON时，回调该方法 可以通过arg1进行消息标记
     */
    public void onTaskON(T object, Message msg) {

    }

    /**
     * 当消息类型为TASK_FAILED时，回调该方法 可以通过arg1进行消息标记
     */
    public void onTaskFailed(T object, Message msg) {

    }

    /**
     * 当消息类型为TASK_CANCELED时，回调该方法处理发送的对象数据 可以通过arg1进行消息标记
     */
    public void onTaskCanceled(T object, Message msg) {

    }
}
