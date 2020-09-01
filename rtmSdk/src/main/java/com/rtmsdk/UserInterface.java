package com.rtmsdk;

import com.rtmsdk.RTMStruct.*;

import java.util.HashSet;
import java.util.List;

public class UserInterface {

    //重连开始接口 每次重连都会判断reloginWillStart 返回值 若返回false则中断重连
    //reloginWillStart 参数说明 uid-用户id  answer本次重连的结果  reloginCount重连次数
    public interface  IReloginStart{
        boolean reloginWillStart(long uid, RTMAnswer answer, int reloginCount);
    }

    //重连完成
    public interface  IReloginCompleted{
        void   reloginCompleted(long uid, boolean successfulm, RTMAnswer answer, int reloginCount);
    }

    //返回空结果的回调接口
    public interface IRTMEmptyCallback {
        void onResult(RTMAnswer answer);
    }

    //泛型接口 带有一个返回值的回调函数
    public interface IRTMCallback<T> {
        void onResult(T t, RTMAnswer answer);
    }

    interface DoubleStringCallback{
        void onResult(String str1, String str2, int errorCode);
    }
}
