package com.fpnn.sdk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shiwangxing on 2017/11/29.
 */
public class ErrorRecorder {
//    static Object  interlock = new Object();
    //-----------------[ Static Properties & Methods ]-------------------
    private static List<ErrorRecorder> instance = new ArrayList<>();
    public  void setErrorRecorder(ErrorRecorder ins) {
//        synchronized (interlock) {
//            instance.add(ins);
//        }
    }

     public  void recordError(String message) {
//         synchronized (interlock) {
//            for (ErrorRecorder er:instance) {
//            if (er != null)
//                er.recordError(message);
//            }
//        }
    }

    public  void recordError(Exception e) {
//        synchronized (interlock) {
//            for (ErrorRecorder er : instance) {
//                if (er != null)
//                    er.recordError(e.getMessage());
//            }
//        }
    }

    public  void recordError(String message, Exception e) {
//        synchronized (interlock) {
//            for (ErrorRecorder er : instance) {
//                if (er != null)
//                    er.recordError(String.format("Error: %s, exception: %s", message, e));
//            }
//        }
//    }
    }
}
