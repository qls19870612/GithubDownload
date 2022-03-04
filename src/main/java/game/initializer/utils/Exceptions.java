package game.initializer.utils;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

import game.initializer.Context;
import game.initializer.InitReq;

/** Created by wyt on 16-11-19. */
public class Exceptions {

    public static RuntimeException exception(Exception e) {
        return new RuntimeException(e);
    }

    public static void throwException(Exception e) {
        Throwables.throwIfUnchecked(e);
        throw exception(e);
    }

    public static RuntimeException exception(String errorMsg) {
        return new RuntimeException(errorMsg);
    }

    public static void throwException(String errorMsg) {
        throw exception(errorMsg);
    }

    public static RuntimeException exception(Context context, String errorMsg) {
        return exception(context.toString() + errorMsg);
    }

    public static void throwException(Context context, String errorMsg) {
        throw exception(context, errorMsg);
    }

    public static void throwExceptionRangeError(long min, long max, Object val, Context context) {
        throwExceptionRangeError(min, max, val, context, "的值");
    }

    public static void throwExceptionRangeError(long min, long max, Object val, Context context, String desc) {
        throwException(context, desc + rangeErrMsg(min, max) + "! " + val);
    }

    public static String rangeErrMsg(long min, long max) {
        return "必须" + (min == max ? "等于" + min : ">=" + min + "且<=" + max);
    }

    public static void checkNotNull(Object fieldValue, InitReq initReq, Context context) {
        if (initReq.notNull()) {
            Preconditions.checkNotNull(fieldValue, "%s不能为null!", context);
        }
    }
}
