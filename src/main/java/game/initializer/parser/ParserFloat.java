package game.initializer.parser;

import java.util.List;

import game.initializer.Context;
import game.initializer.InitReq;
import game.initializer.Parser;
import game.initializer.utils.Configs;

import static game.initializer.utils.Exceptions.throwExceptionRangeError;

/** Created by wyt on 16-11-22. */
public class ParserFloat implements Parser {

    @Override
    public Object parse(Class<?> cls, InitReq initReq, List<String> cfgVals, Context context) {
        if (cls != float.class && cls != Float.class) {
            return Result.UNKNOWN_TYPE;
        }

        if (cfgVals.size() == 0) {
            validate(context.fieldValue(), initReq, context);
            return Result.DEFAULT_VALUE;
        }

        float r = Float.parseFloat(Configs.unwrap(cfgVals, context));
        validate(r, initReq, context);
        return r;
    }

    private void validate(float fieldValue, InitReq initReq, Context context) {
        long min = initReq.min();
        long max = initReq.max();

        if (fieldValue < min || fieldValue > max) {
            throwExceptionRangeError(min, max, fieldValue, context);
        }
    }
}
