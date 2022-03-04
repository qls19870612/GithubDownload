package game.initializer.parser;

import java.util.List;

import game.initializer.Context;
import game.initializer.InitReq;
import game.initializer.Parser;
import game.initializer.utils.Configs;

import static game.initializer.utils.Exceptions.exception;

/** Created by wyt on 16-11-22. */
public class ParserBoolean implements Parser {

    @Override
    public Object parse(Class<?> cls, InitReq initReq, List<String> cfgVals, Context context) {
        if (cls != boolean.class && cls != Boolean.class) {
            return Result.UNKNOWN_TYPE;
        }

        if (cfgVals.size() == 0) {
            return Result.DEFAULT_VALUE;
        }

        String s = Configs.unwrap(cfgVals, context);
        switch (s) {
            case "1":
                return true;
            case "0":
                return false;
            default:
                throw exception(context, "配置的值非法，只能是0或1！ " + s);
        }
    }
}
