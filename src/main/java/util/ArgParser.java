package util;

import com.google.common.collect.Sets;
 

import java.util.Set;


import game.initializer.utils.ObjectParser;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.EMPTY_SET;

public class ArgParser {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final int KEY_MAX_LENGTH = 18;
    private final ObjectParser parser;
    private final boolean showHelp;
    private StringBuilder help;
    public ArgParser(ObjectParser parser, boolean showHelp) {
        this.parser = parser;
        this.showHelp = showHelp;
        help = new StringBuilder("========Help start========\n");
    }

    public static ArgParser parse(String[] args) {

        ObjectParser.Builder builder = ObjectParser.newBuilder();

        boolean showHelp = false;
        for (String s : args) {
            if (s.contains("-h") || s.contains("help")) {
                showHelp = true;
                continue;
            }
            int eqPos = s.indexOf("=");
            checkArgument(eqPos > 0, "参数格式是 key=value: %s", s);

            String key = s.substring(0, eqPos);
            String value = s.substring(eqPos + 1);
            builder.addField(key.trim(), value.trim());
        }

        return new ArgParser( builder.build(),showHelp);
    }

    public String getKey(String key,String desc) {
        if (showHelp) {
            addHelp(key,desc,"string");
            return "";
        }
        return parser.getKey(key);
    }

    private void addHelp(String key, String desc, String type) {
        help.append(desc).append(":\n");
        help.append("\t\t");
        help.append(StringUtils.rightFill(key,KEY_MAX_LENGTH," "))
                .append(StringUtils.rightFill(type,"boolean".length() + 1," "));
        help.append("\n");

    }
    private void addHelp(String key, String desc, String type,String defaultValue) {
        help.append(desc).append(":\n");
        help.append("\t\t");
        help.append(StringUtils.rightFill(key,KEY_MAX_LENGTH," "))
                .append(StringUtils.rightFill(type,"boolean".length() + 1," ")).append("default: ").append(defaultValue);
        help.append("\n");
    }

    public String getKey(String key,String desc,String defaultValue) {
        if (showHelp) {
            addHelp(key, desc, "string",defaultValue);
            return "";
        }
        return parser.getKey(key,defaultValue);
    }

    public boolean getBooleanKey(String key, String desc, boolean defaultValue) {
        if (showHelp) {
            addHelp(key, desc, "boolean",defaultValue?"1":"0");
            return false;
        }
        return parser.getBooleanKey(key,defaultValue);
    }

    public boolean contains(String key) {
        return parser.keySet().contains(key);
    }

    public String[] getList(String key, String desc, String defaultValue) {
        if (showHelp) {
            addHelp(key, desc, "list",defaultValue);
            return EMPTY_STRING_ARRAY;
        }
        String value = parser.getKey(key,defaultValue);
        if (StringUtils.isNotEmpty(value)) {
            return value.split(",");
        }
        return null;
    }

    public String[] getList(String key, String desc) {
        if (showHelp) {
            addHelp(key, desc, "list");
            return EMPTY_STRING_ARRAY;
        }
        String value = parser.getKey(key);
        if (StringUtils.isNotEmpty(value)) {
            return value.split(",");
        }
        return null;
    }

    public Set<String> getSets(String key, String desc, String defaultValue) {
        if (showHelp) {
            addHelp(key, desc, "set",defaultValue);
            return EMPTY_SET;
        }
        String whiteListStr = parser.getKey(key, defaultValue);//白名单不处理
        Set<String> sets;
        if (StringUtils.isNotEmpty(whiteListStr)) {
            String[] split = whiteListStr.split(",");
            sets = Sets.newHashSet(split);
        } else {
            sets = null;
        }
        return sets;
    }

    public boolean showHelp() {
        if (showHelp) {
            help.append("========Help end ========");
            System.out.println(help);
        }
        return showHelp;
    }

    public int getIntKey(String key,String desc) {
        if (showHelp) {
            addHelp(key, desc, "int");
            return 0;
        }
        return parser.getIntKey(key);
    }

    public int getIntKey(String key,String desc, int defaultValue) {
        if (showHelp) {
            addHelp(key, desc, "int",defaultValue+"");
            return 0;
        }
        return parser.getIntKey(key,defaultValue);
    }
}
