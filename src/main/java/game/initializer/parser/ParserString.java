package game.initializer.parser;

import com.google.common.base.Strings;

import java.util.List;

import game.initializer.Context;
import game.initializer.InitReq;
import game.initializer.Parser;
import game.initializer.utils.Configs;

import static com.google.common.base.Preconditions.checkArgument;
import static game.initializer.utils.Exceptions.checkNotNull;

/** Created by wyt on 16-11-22. */
public class ParserString implements Parser {

    @Override
    public Object parse(Class<?> cls, InitReq initReq, List<String> cfgVals, Context context) {
        if (cls != String.class) {
            return Result.UNKNOWN_TYPE;
        }

        if (cfgVals.size() == 0) {
            validate(context.fieldValue(), initReq, context);
            return Result.DEFAULT_VALUE;
        }

        String r = Configs.unwrap(cfgVals, context);
        validate(r, initReq, context);
        return r;
    }

    private void validate(String s, InitReq initReq, Context context) {
        checkNotNull(s, initReq, context);
        checkArgument(!initReq.notEmpty() || !Strings.isNullOrEmpty(s), "%s不能为空！", context);
    }
}
