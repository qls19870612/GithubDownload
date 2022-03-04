package game.initializer;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import game.initializer.Parser.Result;
import game.initializer.parser.ParserArray;
import game.initializer.parser.ParserBoolean;
import game.initializer.parser.ParserDouble;
import game.initializer.parser.ParserEnum;
import game.initializer.parser.ParserFloat;
import game.initializer.parser.ParserInt;
import game.initializer.parser.ParserList;
import game.initializer.parser.ParserLong;
import game.initializer.parser.ParserString;
import game.initializer.utils.ParserProxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static game.initializer.utils.Exceptions.throwException;

class Utils {

    private static final Parser builtIn = ParserProxy
            .of(new ParserInt(), new ParserLong(), new ParserFloat(), new ParserDouble(), new ParserBoolean(), new ParserString(), new ParserEnum(),
                    new ParserArray(), new ParserList());

    static Parser builtInParser() {
        return builtIn;
    }

    static Parser fieldParser(InitReq initReq, Context context) {
        Parser p = context.initializer.parser;

        Class<? extends Parser>[] cs = initReq.parsers();
        if (cs.length == 0) {
            return p;
        }

        Parser[] ps = new Parser[cs.length];
        ParserGetter pg = context.initializer.parserGetter;

        for (int i = 0; i < cs.length; i++) {
            Class<? extends Parser> c = cs[i];

            Parser p2 = pg != null ? pg.get(c) : null;
            if (p2 == null) {
                try {
                    p2 = cs[i].newInstance();
                } catch (Exception e) {
                    throwException(e);
                }
            }
            ps[i] = p2;
        }
        return p.chain(ps);
    }

    static void checkParseResult(Object value, Type type, List<String> cfgVals, Context context) {
        checkState(value != Result.UNKNOWN_TYPE, "%s的类型 \"%s\" 无法解析!", context, type.getTypeName());
    }

    static String[] fieldCfgKeys(Field field, InitReq initReq) {
        if (initReq.cfgKeys().length > 0) {
            if (Strings.isNullOrEmpty(initReq.cfgPrefix())) {
                return initReq.cfgKeys();
            }
            String[] str = new String[initReq.cfgKeys().length];
            for (int i = 0; i < initReq.cfgKeys().length; i++) {
                str[i] = initReq.cfgPrefix() + "_" + initReq.cfgKeys()[i];
            }
            return str;
        }
        return new String[]{initReq.cfgPrefix() + field.getName()};
    }

    static List<String> fieldCfgVals(ConfigSupplier cfg, String[] keys, InitReq initReq, Context context) {
        List<String> vals = cfg.get(keys);
        if (vals == null || vals.size() == 0) {
            return ImmutableList.of();
        }

        String sep = initReq.cfgSplitSep();
        if (!sep.isEmpty()) {
            checkArgument(keys.length == 1, "%s的注解如果cfgSplitSep不为空，则cfgKeys只允许有一列！%s", context); // 如果key有多列，一般使用场景是每n列是解析出来一个对象，该情况要保留配置原始状态

            ImmutableList.Builder<String> b = ImmutableList.builder();

            for (String v : vals) {
                String[] ss = v.split(sep);
                for (String s : ss) {
                    s = s.trim();
                    if (!s.isEmpty()) {
                        b.add(s);
                    }
                }
            }
            return b.build();
        }

        if (keys.length > 1) {
            return vals; // 不trim，原因如上
        }

        List<String> r = null;
        for (int i = 0; i < vals.size(); i++) {
            String s = vals.get(i).trim();

            if (Strings.isNullOrEmpty(s)) { // 居然这里有空的元素
                if (r == null) {
                    r = new ArrayList<>(vals.size());
                    for (int j = 0; j < i; j++) {
                        r.add(vals.get(j));
                    }
                }
                continue;
            }

            if (r != null) {
                r.add(s);
            }
        }

        return r == null ? vals : r;
    }
}
