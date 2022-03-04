package game.initializer.utils;

/** Created by wyt on 16-11-18. */
public class Defaults {

    public static int defaultInt() {
        return defaultInt(0);
    }

    public static int defaultInt(int i) {
        return i;
    }

    public static long defaultLong() {
        return defaultLong(0L);
    }

    public static long defaultLong(long l) {
        return l;
    }

    public static float defaultFloat() {
        return defaultFloat(0F);
    }

    public static float defaultFloat(float f) {
        return f;
    }

    public static double defaultDouble() {
        return defaultDouble(0);
    }

    public static double defaultDouble(double d) {
        return d;
    }

    public static boolean defaultBoolean() {
        return defaultBoolean(false);
    }

    public static boolean defaultBoolean(boolean b) {
        return b;
    }

    public static String defaultStr() {
        return defaultStr("");
    }

    public static String defaultStr(String s) {
        return s;
    }

    public static <T> T defaultObj() {
        return defaultObj(null);
    }

    public static <T> T defaultObj(T o) {
        return o;
    }

    // 数组相关
    private static final int[] emptyIntArray = new int[0];

    private static final long[] emptyLongArray = new long[0];

    private static final String[] emptyStrArray = new String[0];

    public static int[] defaultIntArray() {
        return defaultIntArray(emptyIntArray);
    }

    public static int[] defaultIntArray(int[] i) {
        return i;
    }

    public static long[] defaultLongArray() {
        return defaultLongArray(emptyLongArray);
    }

    public static long[] defaultLongArray(long[] i) {
        return i;
    }

    public static String[] defaultStrArray() {
        return defaultStrArray(emptyStrArray);
    }

    public static String[] defaultStrArray(String[] s) {
        return s;
    }
}
