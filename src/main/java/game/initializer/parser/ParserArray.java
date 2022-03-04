package game.initializer.parser;

import java.lang.reflect.Array;
import java.util.List;

import game.initializer.Context;
import game.initializer.InitReq;
import game.initializer.Parser;

import static game.initializer.utils.Exceptions.checkNotNull;
import static game.initializer.utils.Exceptions.throwExceptionRangeError;

/** Created by wyt on 16-11-22. */
public class ParserArray implements Parser {

    @Override
    public Object parse(Class<?> cls, InitReq initReq, List<String> cfgVals, Context context) {
        if (!cls.isArray()) {
            return Result.UNKNOWN_TYPE;
        }

        if (cfgVals.size() == 0) {
            validate(context.fieldValue(), initReq, context);
            return Result.DEFAULT_VALUE;
        }

        Class<?> componentType = cls.getComponentType();
        Object r = Array.newInstance(componentType, cfgVals.size());

        for (int i = 0; i < cfgVals.size(); i++) {
            Object v = context.parse(componentType, initReq, cfgVals.get(i));
            Array.set(r, i, v);
        }

        validate(r, initReq, context);
        return r;
    }

    private void validate(Object fieldValue, InitReq initReq, Context context) {
        checkNotNull(fieldValue, initReq, context);

        if (fieldValue != null) {
            int min = initReq.minLen();
            int max = initReq.maxLen();
            int len = Array.getLength(fieldValue);

            if (len < min || len > max) {
                throwExceptionRangeError(min, max, len, context, "数组长度");
            }
        }
    }
}
