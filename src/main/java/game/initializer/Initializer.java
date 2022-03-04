package game.initializer;

import java.lang.reflect.Field;
import java.util.List;

import game.initializer.Parser.Result;

import static game.initializer.Utils.builtInParser;
import static game.initializer.Utils.fieldCfgVals;
import static game.initializer.utils.Exceptions.throwException;

public class Initializer {

    public static Initializer of(Parser... ps) {
        return of(null, ps);
    }

    public static Initializer of(ParserGetter pg, Parser... ps) {
        return new Initializer(pg, ps);
    }

    final Parser parser;
    final ParserGetter parserGetter;

    private Initializer(ParserGetter parserGetter, Parser... ps) {
        this.parser = builtInParser().chain(ps);
        this.parserGetter = parserGetter;
    }

    public void initialize(Object obj, ConfigSupplier cfg) {
        Context context = Context.of(this, obj, cfg);
        for (Class<?> cls = obj.getClass(); cls != null; cls = cls.getSuperclass()) {
            initialize(context.cls(cls));
        }
    }

    public <T> void initialize(T obj, Class<? super T> cls, ConfigSupplier cfg) {
        initialize(Context.of(this, obj, cfg).cls(cls));
    }

    private void initialize(Context context) {
        while (context.next()) {
            Field field = context.field;
            boolean isAccessible = field.isAccessible();

            try {
                field.setAccessible(true);
                initialize(field, context.initReq, context);
            } catch (Exception e) {
                throwException(e);
            } finally {
                field.setAccessible(isAccessible);
            }
        }
    }

    private void initialize(Field field, InitReq initReq, Context context) throws Exception {
        List<String> cfgVals = fieldCfgVals(context.cfg, context.fieldCfgKeys, initReq, context);
        Object value = context.parse(field.getGenericType(), initReq, cfgVals);

        if (value != Result.DEFAULT_VALUE) {
            field.set(context.obj, value);
        }
    }
}
