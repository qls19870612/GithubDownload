package game.initializer.parser;

import com.google.common.collect.ImmutableList;

import java.lang.reflect.Type;
import java.util.List;

import game.initializer.Context;
import game.initializer.InitReq;
import game.initializer.Parser;
import game.initializer.utils.Types;

import static game.initializer.utils.Exceptions.checkNotNull;
import static game.initializer.utils.Exceptions.throwExceptionRangeError;

/** Created by wyt on 16-11-22. */
public class ParserList implements Parser {

    @SuppressWarnings("unchecked")
    @Override
    public Object parse(Type type, InitReq initReq, List<String> cfgVals, Context context) {
        Type argType = Types.parameterizedTypeArgTypesUnwrap(type, List.class, ImmutableList.class);
        if (argType == null) {
            return Result.UNKNOWN_TYPE;
        }

        if (cfgVals.size() == 0) {
            validate(context.fieldValue(), initReq, context);
            return Result.DEFAULT_VALUE;
        }

        ImmutableList.Builder b = ImmutableList.builder();
        for (String cfgVal : cfgVals) {
            b.add(context.parse(argType, initReq, cfgVal));
        }

        ImmutableList r = b.build();
        validate(r, initReq, context);
        return r;
    }

    private void validate(List list, InitReq initReq, Context context) {
        checkNotNull(list, initReq, context);

        if (list != null) {

            int min = initReq.minSize();
            int max = initReq.maxSize();
            int size = list.size();

            if (size < min || size > max) {
                throwExceptionRangeError(min, max, size, context, "list元素个数");
            }
        }
    }
}
