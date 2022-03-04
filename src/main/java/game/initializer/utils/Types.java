package game.initializer.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.google.common.base.Preconditions.checkArgument;

/** Created by wyt on 16-11-23. */
public class Types {

    public static Type[] parameterizedTypeArgTypes(Type type, Class<?>... rawClss) {
        if (!(type instanceof ParameterizedType)) {
            return null;
        }

        ParameterizedType parameterizedType = (ParameterizedType) type;
        Class<?> cls = (Class<?>) parameterizedType.getRawType();

        boolean isRightRawCls = false;
        for (Class<?> rawCls : rawClss) {
            if (cls == rawCls) {
                isRightRawCls = true;
                break;
            }
        }

        if (!isRightRawCls) {
            return null;
        }

        return parameterizedType.getActualTypeArguments();
    }

    public static Type parameterizedTypeArgTypesUnwrap(Type type, Class<?>... rawClss) {
        Type[] types = parameterizedTypeArgTypes(type, rawClss);
        if (types == null) {
            return null;
        }
        checkArgument(types.length == 1);
        return types[0];
    }
}
