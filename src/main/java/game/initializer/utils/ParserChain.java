package game.initializer.utils;

import java.lang.reflect.Type;
import java.util.List;

import game.initializer.Context;
import game.initializer.InitReq;
import game.initializer.Parser;

/** Created by wyt on 16-11-23. */
public class ParserChain implements Parser {

    public static Parser of(Parser parent, Parser... children) {
        if (children == null || children.length == 0) {
            return parent;
        }
        return new ParserChain(parent, children);
    }

    private final Parser parent;
    private final Parser child;

    private ParserChain(Parser parent, Parser... children) {
        this.parent = parent;
        this.child = ParserProxy.of(children);
    }

    @Override
    public Object parse(Type type, InitReq initReq, List<String> cfgVals, Context context) {
        Object r = child.parse(type, initReq, cfgVals, context);
        if (r != Result.UNKNOWN_TYPE) {
            return r;
        }
        return parent.parse(type, initReq, cfgVals, context);
    }

    @Override
    public Object parse(Class<?> cls, InitReq initReq, List<String> cfgVals, Context context) {
        Object r = child.parse(cls, initReq, cfgVals, context);
        if (r != Result.UNKNOWN_TYPE) {
            return r;
        }
        return parent.parse(cls, initReq, cfgVals, context);
    }
}
