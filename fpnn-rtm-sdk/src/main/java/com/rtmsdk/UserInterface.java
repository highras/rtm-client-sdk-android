package com.rtmsdk;

import com.rtmsdk.RTMStruct.RTMAnswer;

public class UserInterface {
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
