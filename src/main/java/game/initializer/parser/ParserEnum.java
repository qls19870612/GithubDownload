package game.initializer.parser;
 
import com.google.common.primitives.Ints; 

import java.util.Arrays;
import java.util.List;

import game.initializer.Context;
import game.initializer.InitReq;
import game.initializer.Parser;
import game.initializer.utils.Configs;

import static game.initializer.utils.Exceptions.checkNotNull;
import static game.initializer.utils.Exceptions.exception;

/** Created by wyt on 16-11-22. */
public class ParserEnum implements Parser {

    @SuppressWarnings("unchecked")
    @Override
    public Object parse(Class<?> cls, InitReq initReq, List<String> cfgVals, Context context) {
        if (!cls.isEnum()) {
            return Result.UNKNOWN_TYPE;
        }

        if (cfgVals.size() == 0) {
            validate(context.fieldValue(), initReq, context);
            return Result.DEFAULT_VALUE;
        }

        Enum r = doParse((Class<? extends Enum>) cls, cfgVals, context);
        validate(r, initReq, context);
        return r;
    }

    private Enum doParse(Class<? extends Enum> cls, List<String> cfgVals, Context context) {

        String s = Configs.unwrap(cfgVals, context);
        Enum[] types = cls.getEnumConstants();

        Integer val = Ints.tryParse(s);
        if (val == null) {
            for (Enum type : types) {
                if (type.name().equalsIgnoreCase(s)) {
                    return type;
                }
            }
            throw exception(context, "配置的值无法转成int，但又不是enum中的字符串！ 配置的值是: " + s + ", 合法的字符串值是: " + Arrays.toString(types));
        }

        
        // 只是个普通的enum
        if (val >= 0 && val < types.length) {
            return types[val];
        }

        throw exception(context, "数值范围必须在>=0且<" + types.length + "！ 配置的值是" + s);
    }

    private void validate(Enum fieldValue, InitReq initReq, Context context) {
        checkNotNull(fieldValue, initReq, context);
    }
}
