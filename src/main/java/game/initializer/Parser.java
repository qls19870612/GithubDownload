package game.initializer;

import java.lang.reflect.Type;
import java.util.List;

import game.initializer.utils.ParserChain;

public interface Parser {

    default Object parse(Type type, InitReq initReq, List<String> cfgVals, Context context) {
        if (type instanceof Class<?>) {
            return parse((Class<?>) type, initReq, cfgVals, context);
        }
        return Result.UNKNOWN_TYPE;
    }

    default Object parse(Class<?> cls, InitReq initReq, List<String> cfgVals, Context context) {
        return Result.UNKNOWN_TYPE;
    }

    default Parser chain(Parser... ps) {
        return ParserChain.of(this, ps);
    }

    // 需要特殊处理的一些结果

    enum Result {
        UNKNOWN_TYPE, DEFAULT_VALUE,
    }
}
