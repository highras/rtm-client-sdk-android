package com.rtmsdk;

import com.rtmsdk.RTMStruct.RTMAnswer;

public class UserInterface {

    //重连开始接口 每次重连都会判断reloginWillStart 返回值 若返回false则中断重连
    //reloginWillStart 参数说明 uid-用户id  answer本次重连的结果  reloginCount重连次数
    //备注 需要用户设定一些条件 比如重连间隔 最大重连次数
    public interface  IReloginStart{
        boolean reloginWillStart(long uid, RTMAnswer answer, int reloginCount);
    }

    //重连完成(如果 successful 为false表示最终重连失败,answer会有详细的错误码和错和错误信息 为true表示重连成功)
    //备注 当用户的token过期 重连会直接返回 不会继续判断reloginWillStart
    public interface  IReloginCompleted{
        void   reloginCompleted(long uid, boolean successful, RTMAnswer answer, int reloginCount);
    }

    //返回空结果的回调接口
    public interface IRTMEmptyCallback {
        void onResult(RTMAnswer answer);
    }

    //泛型接口 带有一个返回值的回调函数
    public interface IRTMCallback<T> {
        void onResult(T t, RTMAnswer answer);
    }

    //泛型接口 带有两个返回值的回调函数
    public interface IRTMDoubleValueCallback<T,V> {
        void onResult(T t, V v, RTMAnswer answer);
    }
}
