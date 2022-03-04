package game.initializer.parser;

import java.util.List;

import game.initializer.Context;
import game.initializer.InitReq;
import game.initializer.Parser;
import game.initializer.utils.Configs;

import static game.initializer.utils.Exceptions.throwExceptionRangeError;

/** Created by wyt on 16-11-22. */
public class ParserLong implements Parser {
    public static final long CLIENT_MAX_LONG = (1L << 52) - 1;

    @Override
    public Object parse(Class<?> cls, InitReq initReq, List<String> cfgVals, Context context) {
        if (cls != long.class && cls != Long.class) {
            return Result.UNKNOWN_TYPE;
        }

        if (cfgVals.size() == 0) {
            validate(context.fieldValue(), initReq, context);
            return Result.DEFAULT_VALUE;
        }

        long r = Long.parseLong(Configs.unwrap(cfgVals, context));
        validate(r, initReq, context);
        return r;
    }

    private void validate(long fieldValue, InitReq initReq, Context context) {
        long min = initReq.min();
        long max = initReq.max() != Integer.MAX_VALUE ? initReq.max() : CLIENT_MAX_LONG;

        if (fieldValue < min || fieldValue > max) {
            throwExceptionRangeError(min, max, fieldValue, context);
        }
    }
}
