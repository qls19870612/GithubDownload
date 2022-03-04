package game.initializer.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

import java.util.List;

import game.initializer.Context;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/** Created by wyt on 16-12-6. */
public class Configs {
    private static final String empty = "";

    public static String unwrap(List<String> list, Context context) {
        if (list.size() == 0) {
            return empty;
        }
        checkArgument(list.size() == 1, "%s Configs.unwrap 时，list.size不等于1! %s", context, list.size());
        return list.get(0);
    }

    public static int unwrapToInt(List<String> list, Context context) {
        if (list.size() == 0) {
            return 0;
        }
        String s = unwrap(list, context);
        return checkNotNull(Ints.tryParse(s), "%s Configs.unwrapToInt 时，字符串无法转成int! %s", context, s);
    }

    public static List<String> wrap(String s) {
        return ImmutableList.of(s);
    }
}
