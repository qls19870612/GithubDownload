package game.initializer.utils;

import com.google.common.collect.LinkedListMultimap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import game.collection.IntArrayList;
import game.collection.IntPair;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class ObjectParser {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    final LinkedListMultimap<String, String> map;

    public ObjectParser(String[] fields, String[] values) {
        this("", fields, values);
    }

    public ObjectParser(String fileName, String[] fields, String[] values) {
        super();
        checkArgument(fields.length == values.length, "fields count must equals to values count. fileName: %s fields count: %s. values count: %s. \nfield:%s\n value: %s", fileName, fields.length,
                values.length, Arrays.toString(fields), Arrays.toString(values));

        map = LinkedListMultimap.create(fields.length);

        int len = fields.length;
        for (int i = 0; i < len; i++) {
            map.put(fields[i], values[i].trim());
        }
    }

    private ObjectParser(LinkedListMultimap<String, String> map) {
        this.map = map;
    }

    public int keyCount() {
        return map.size();
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public String getKey(String key, String defaultValue) {
        checkNotNull(key);
        if (!map.containsKey(key)) {
            return defaultValue;
        }
        List<String> result = map.get(key);
        checkArgument(result.size() == 1, "field [%s] should not have multiple occursion, now: %s", key, result.size());
        return result.get(0);
    }

    public String getKey(String key) {
        checkNotNull(key);
        checkArgument(map.containsKey(key), "field not present: %s", key);

        List<String> result = map.get(key);
        checkArgument(result.size() == 1, "field [%s] should not have multiple occursion, now: %s", key, result.size());
        return result.get(0);
    }

    public List<String> getKeyList(String key) {
        checkNotNull(key);
        if (!map.containsKey(key)) {
            return Collections.emptyList();
        }
        List<String> result = map.get(key);
        return result;
    }

    public String[] getStringArray(String key) {
        List<String> value = getKeyList(key);
        return value.toArray(EMPTY_STRING_ARRAY);
    }

    public String[] getStringArray(String key, String[] defaultValue) {
        checkNotNull(key);
        if (!map.containsKey(key)) {
            return defaultValue;
        }

        return getStringArray(key);
    }

    public int getIntKey(String key, int defaultValue) {
        checkNotNull(key);
        if (!map.containsKey(key)) {
            return defaultValue;
        }
        List<String> result = map.get(key);
        checkArgument(result.size() == 1, "field [%s] should not have multiple occursion, now: %s", key, result.size());
        String v = result.get(0);
        if ("".equals(v)) {
            return 0;
        }
        return Integer.parseInt(v);
    }

    public int getIntKey(String key) {
        String value = getKey(key);
        if ("".equals(value)) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    public long getLongKey(String key) {
        String value = getKey(key);
        if ("".equals(value)) {
            return 0;
        }
        return Long.parseLong(value);
    }

    public long getLongKey(String key, long defaultValue) {
        checkNotNull(key);
        if (!map.containsKey(key)) {
            return defaultValue;
        }
        List<String> result = map.get(key);
        checkArgument(result.size() == 1, "field [%s] should not have multiple occursion, now: %s", key, result.size());
        String v = result.get(0);
        if ("".equals(v)) {
            return 0;
        }
        return Long.parseLong(v);
    }

    public float getFloatKey(String key) {
        String value = getKey(key);
        if ("".equals(value)) {
            return 0;
        }
        return Float.parseFloat(value);
    }

    public float getFloatKey(String key, float defaultValue) {
        checkNotNull(key);
        if (!map.containsKey(key)) {
            return defaultValue;
        }
        List<String> result = map.get(key);
        checkArgument(result.size() == 1, "field [%s] should not have multiple occursion, now: %s", key, result.size());
        String v = result.get(0);
        if ("".equals(v)) {
            return 0f;
        }
        return Float.parseFloat(v);
    }

    public boolean getBooleanKey(String key, boolean defaultValue) {
        int v = getIntKey(key, defaultValue ? 1 : 0);
        return v == 1;
    }

    public boolean getBooleanKey(String key) {
        int v = getIntKey(key);
        return v == 1;
    }

    public IntArrayList getIntKeyList(String key) {
        List<String> value = getKeyList(key);
        IntArrayList result = new IntArrayList(value.size());
        for (String s : value) {
            if ("".equals(s)) {
                result.add(0);
                continue;
            }

            result.add(Integer.parseInt(s));
        }
        return result;
    }

    public int[] getIntKeyArray(String key) {
        List<String> value = getKeyList(key);
        int[] result = new int[value.size()];
        int i = 0;
        for (String s : value) {
            int slot = i++;
            if ("".equals(s)) {
                result[slot] = 0;
                continue;
            }

            result[slot] = Integer.parseInt(s);
        }
        return result;
    }

    public float[] getFloatKeyArray(String key) {
        List<String> value = getKeyList(key);
        float[] result = new float[value.size()];
        int i = 0;
        for (String s : value) {
            int slot = i++;
            if ("".equals(s)) {
                result[slot] = 0;
                continue;
            }

            result[slot] = Float.parseFloat(s);
        }
        return result;
    }

    public IntPair getIntPairKey(String key1, String key2) {
        int value1 = getIntKey(key1);
        int value2 = getIntKey(key2);
        return new IntPair(value1, value2);
    }

    public int[] getIntArrayKey(String... keys) {
        checkArgument(keys.length > 0, "ObjectParser.getIntArrayKey() 没有参数!");

        int[] result = new int[keys.length];
        for (int idx = 0; idx < keys.length; idx++) {
            String key = keys[idx];
            result[idx] = getIntKey(key);
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ObjectParser) {
            return map.equals(((ObjectParser) obj).map);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public String toString() {
        return map.toString();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private final LinkedListMultimap<String, String> map;

        private Builder() {
            map = LinkedListMultimap.create();
        }

        public Builder addField(String header, String field) {
            map.put(header, field);
            return this;
        }

        public Builder addField(String header, int field) {
            map.put(header, String.valueOf(field));
            return this;
        }

        /**
         * 调用完后再改变这个builder, 也会改变之前build出来的ObjectParser
         *
         * @return
         */
        public ObjectParser build() {
            return new ObjectParser(map);
        }
    }
}
