package game.initializer;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/** Created by wyt on 16-12-1. */
public class Config implements ConfigSupplier {

    public static final Config EMPTY = builder("empty", false).build();

    private final String name;
    private final boolean keyMustExists;
    private final ImmutableListMultimap<String, String> multimap;
 

    public Config(String name, boolean keyMustExists, ImmutableListMultimap<String, String> multimap) {
        this.name = name;
        this.keyMustExists = keyMustExists;
        this.multimap = multimap;
 
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int defaultVal) {
        String val = getStr(key, defaultVal + "");
        return checkNotNull(Ints.tryParse(val), "%s中的%s字段对应的配置无法解析成int！%s", this, key, val);
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public long getLong(String key, long defaultVal) {
        String val = getStr(key, defaultVal + "");
        return checkNotNull(Longs.tryParse(val), "%s中的%s字段对应的配置无法解析成long！%s", this, key, val);
    }

    public float getFloat(String key) {
        return getFloat(key, 0);
    }

    public float getFloat(String key, float defaultVal) {
        String val = getStr(key, defaultVal + "");
        return checkNotNull(Floats.tryParse(val), "%s中的%s字段对应的配置无法解析成float！%s", this, key, val);
    }

    public double getDouble(String key) {
        return getDouble(key, 0);
    }

    public double getDouble(String key, double defaultVal) {
        String val = getStr(key, defaultVal + "");
        return checkNotNull(Doubles.tryParse(val), "%s中的%s字段对应的配置无法解析成double！%s", this, key, val);
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultVal) {
        return getInt(key, defaultVal ? 1 : 0) == 1;
    }

    public String getStr(String key) {
        return getStr(key, "");
    }

    public String getStr(String key, String defaultVal) {
        List<String> list = getStrList(key);
        if (list.size() == 0) {
            return defaultVal;
        }

        checkArgument(list.size() == 1, "%s中的%s字段只能有一列！%s", this, key, list.size());
        String s = list.get(0);

        if (Strings.isNullOrEmpty(s)) {
            return defaultVal;
        }
        return s;
    }

    public ImmutableList<String> getStrList(String key) {
        List<String> list = Lists.newArrayListWithCapacity(1);
        list.addAll(multimap.get(key));
        for (int i = 1; i < 100; i++) {
            ImmutableList<String> immutableList = multimap.get(key + i);
            if (immutableList.size() <= 0) {
                break;
            }
            list.addAll(immutableList);
        }
        checkArgument(!keyMustExists || list.size() > 0, "\"%s\"中的\"%s\"字段没有找到！", this, key);
        return ImmutableList.copyOf(list);
    }

    public ImmutableList<String> getStrListOmitEmpty(String key) {
        ImmutableList<String> list = getStrList(key);
        return ImmutableList.copyOf(Iterables.filter(list, s -> !Strings.isNullOrEmpty(s)));
    }

    public int[] getIntArray(String key) {
        ImmutableList<String> list = getStrList(key);
        int[] r = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            String val = list.get(i);
            if (Strings.isNullOrEmpty(val)) {
                r[i] = 0;
            } else {
                r[i] = checkNotNull(Ints.tryParse(val), "%s中的%s字段对应的配置无法解析成int！%s", this, key, val);
            }
        }
        return r;
    }

    public int[] getIntArrayOmitEmpty(String key) {
        ImmutableList<String> list = getStrListOmitEmpty(key);
        int[] r = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            String val = list.get(i);
            r[i] = checkNotNull(Ints.tryParse(val), "%s中的%s字段对应的配置无法解析成int！%s", this, key, val);
        }
        return r;
    }

    public ImmutableSet<String> keySet() {
        return multimap.keySet();
    }

    @Override
    public List<String> get(String... keys) {
        if (keys.length == 1) {
            return getStrList(keys[0]);
        }

        List<List<String>> list = new ArrayList<>(keys.length);
        for (String key : keys) {
            list.add(getStrList(key));
            int size = list.size();
            if (size > 1) {
                checkArgument(list.get(size - 1).size() == list.get(size - 2).size(), "%s中的配置列%s为相关联的一组列，不能缺少！", this, Arrays.toString(keys));
            }
        }

        ImmutableList.Builder<String> b = ImmutableList.builder();
        for (int j = 0; j < list.get(0).size(); j++) {
            for (int i = 0; i < list.size(); i++) {
                b.add(list.get(i).get(j)); // 把一组中的所有列放一起
            }
        }
        return b.build();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Config)) {
            return false;
        }
        return  false;
    }

    public static Builder builder(String name, boolean keyMustExists) {
        return new Builder(name, keyMustExists);
    }

    public static class Builder {

        private final String name;
        private final boolean keyMustExists;
        private final ImmutableListMultimap.Builder<String, String> b;

        private Builder(String name, boolean keyMustExists) {
            this.name = name;
            this.keyMustExists = keyMustExists;
            this.b = ImmutableListMultimap.builder();
        }

        public Builder add(String key, String val) {
            key = key.trim();
            if (!key.isEmpty()) {
                b.put(key, val);
            }
            return this;
        }

        public Config build() {
            return new Config(name, keyMustExists, b.build());
        }
    }
}
