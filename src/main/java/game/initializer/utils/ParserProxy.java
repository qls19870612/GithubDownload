package game.initializer.utils;

import java.lang.reflect.Type;
import java.util.List;

import game.initializer.Context;
import game.initializer.InitReq;
import game.initializer.Parser;

 

/** Created by wyt on 16-11-22. */
public class ParserProxy implements Parser {

    public static Parser of(Parser... ps) {
        
        if (ps.length == 1) {
            return ps[0];
        }
        return new ParserProxy(ps);
    }

    private final Parser[] ps;

    private ParserProxy(Parser... ps) {
        this.ps = ps;
    }

    @Override
    public Object parse(Type type, InitReq initReq, List<String> cfgVals, Context context) {
        for (Parser p : ps) {
            Object r = p.parse(type, initReq, cfgVals, context);
            if (r != Result.UNKNOWN_TYPE) {
                return r;
            }
        }
        return Result.UNKNOWN_TYPE;
    }

    @Override
    public Object parse(Class<?> cls, InitReq initReq, List<String> cfgVals, Context context) {
        for (Parser p : ps) {
            Object r = p.parse(cls, initReq, cfgVals, context);
            if (r != Result.UNKNOWN_TYPE) {
                return r;
            }
        }
        return Result.UNKNOWN_TYPE;
    }
}
