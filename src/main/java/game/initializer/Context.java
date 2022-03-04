package game.initializer;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterators;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import game.initializer.Parser.Result;
import game.initializer.utils.Configs;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static game.initializer.Utils.checkParseResult;
import static game.initializer.Utils.fieldCfgKeys;
import static game.initializer.Utils.fieldParser;
import static game.initializer.utils.Exceptions.exception;

/** Created by wyt on 16-11-21. */
public class Context {

    static Context of(Initializer initializer, Object obj, ConfigSupplier cfg) {
        return new Context(initializer, obj, cfg);
    }

    final Initializer initializer;
    final Object obj;
    public final ConfigSupplier cfg;
    private Iterator<Field> fieldItr;

    Field field;
    InitReq initReq;
    String[] fieldCfgKeys;
    Parser fieldParser;

    private Context(Initializer initializer, Object obj, ConfigSupplier cfg) {
        this.initializer = initializer;
        this.obj = obj;
        this.cfg = cfg;
    }

    Context cls(Class<?> cls) {
        checkArgument(cls.isInstance(obj), "要初始化的对象和类不兼容！ %s, %s", this, cls.getName());
        this.fieldItr = toFieldIterator(cls);
        return this;
    }

    boolean next() {
        if (!fieldItr.hasNext()) {
            return false;
        }

        field = fieldItr.next();
        initReq = field.getAnnotation(InitReq.class);
        fieldCfgKeys = fieldCfgKeys(field, initReq);
        fieldParser = fieldParser(initReq, this);

        checkArgument(!Modifier.isStatic(field.getModifiers()), "%s不能是static的!", this);
        return true;
    }

    public Object parse(Type type, InitReq initReq, String cfgVal) {
        return parse(type, initReq, Configs.wrap(cfgVal));
    }

    public Object parse(Type type, InitReq initReq, List<String> cfgVals) {
        if (type != field.getGenericType()) { // 递归解析
            checkArgument(cfgVals.size() > 0, "%s在解析%s类型时，配置信息居然为空！", this, type.getTypeName());
        }

        Object obj = fieldParser.parse(type, initReq, cfgVals, this);
        checkParseResult(obj, type, cfgVals, this);
        if (initReq.notNull()) {
            checkNotNull(obj, "%s配置的值非法! %s", this, cfgVals);
        }

        if (cfgVals.size() > 0) {
            checkArgument(obj != Result.DEFAULT_VALUE, "%s在解析%s类型时，配置信息不为空，返回的却是默认值！%s", this, type.getTypeName(), cfgVals);
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    public <T> T fieldValue() {
        try {
            return (T) field.get(obj);
        } catch (Exception e) {
            Throwables.throwIfUnchecked(e);
            throw exception(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(obj.getClass().getSimpleName());
        b.append(".class -> \"").append(obj).append("\" ");

        if (field != null) {
            ToStringHelper h = MoreObjects.toStringHelper(field.getName());
            h.add("type", field.getGenericType().getTypeName());
            h.add("cfgKeys", Arrays.toString(fieldCfgKeys));
            h.add("desc", initReq.desc());

            b.append("中的 \"").append(h.toString()).append("\" 字段");
        }
        return b.toString();
    }

    private static Iterator<Field> toFieldIterator(Class<?> cls) {
        return Iterators.filter(Iterators.forArray(cls.getDeclaredFields()), field -> {
            InitReq initReq = field.getAnnotation(InitReq.class);
            return initReq != null && !initReq.skip();
        });
    }
}
