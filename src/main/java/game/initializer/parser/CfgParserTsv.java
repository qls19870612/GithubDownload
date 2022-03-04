package game.initializer.parser;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

import game.initializer.Config;

import static com.google.common.base.Preconditions.checkArgument;

/** Created by wyt on 16-12-8. */
public class CfgParserTsv { // https://en.wikipedia.org/wiki/Tab-separated_values

    public static List<Config> parse(String s, String name) {
        return of(s, name).parse();
    }

    public static CfgParserTsv of(String s, String name) {
        ImmutableList.Builder<String> b = ImmutableList.builder();

        int li = 0;
        boolean isInQuotes = false;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            checkArgument(c != '\r', "TSV格式的配置文件中，居然包含'\\r'字符! %s", name);

            if (c == '"') {
                isInQuotes = !isInQuotes;
                continue;
            }

            if (isInQuotes) { // 引号中的字符不做处理
                checkArgument(c != '\t', "TSV格式的配置文件中，引号里的内容居然有tab键！%s", name);
                continue;
            }

            if (c == '\n') {
                String line = s.substring(li, i).trim();
                if (!line.isEmpty()) {
                    b.add(line);
                }
                li = i + 1;
            }
        }
        checkArgument(!isInQuotes, "TSV格式的配置文件中，引号的数量居然不是成对的！配置文件: %s", name);

        if (li < s.length()) {
            String line = s.substring(li, s.length()).trim();
            if (!line.isEmpty()) {
                b.add(line);
            }
        }

        return new CfgParserTsv(name, b.build());
    }

    private static final CharMatcher VALID_KEY_CHARS =
            CharMatcher.inRange('a', 'z').or(CharMatcher.inRange('A', 'Z')).or(CharMatcher.inRange('0', '9')).or(CharMatcher.is('_'))
                    .or(CharMatcher.is(' ')).or(CharMatcher.is('\t'));

    private static final Splitter SPLITTER_TAB = Splitter.on('\t').trimResults();

    private final String name;
    public final String descLine;
    public final String keyLine;
    public final ImmutableList<String> valLines;

    private CfgParserTsv(String name, ImmutableList<String> lines) {
        this.name = name;
        checkArgument(lines.size() >= 2, "TSV格式的配置文件至少要有两行！配置文件: %s, 行数: %s", this, lines.size());

        this.descLine = lines.get(0);
        this.keyLine = lines.get(1);
        checkArgument(keyLine.length() > 0, "TSV格式的配置文件字段行不能为空！配置文件: %s", this);
        checkArgument(VALID_KEY_CHARS.matchesAllOf(keyLine), "TSV格式的配置文件字段行包含非法字符，只能是字母、数组、下划线！ 配置文件: %s, 字段行: %s", name, keyLine);

        this.valLines = lines.subList(2, lines.size());
    }

    public List<Config> parse() {
        if (valLines.size() == 0) {
            return ImmutableList.of();
        }

        List<Config> r = Lists.newArrayListWithCapacity(valLines.size());
        List<String> keys = SPLITTER_TAB.splitToList(keyLine);

        for (String valLine : valLines) {
            if (valLine.isEmpty()) {
                continue;
            }

            List<String> vals = SPLITTER_TAB.splitToList(valLine);
            Config.Builder b = Config.builder(name, false);

            for (int i = 0; i < keys.size(); i++) {
                b.add(keys.get(i), i < vals.size() ? vals.get(i) : "");
            }
            r.add(b.build());
        }

        return r;
    }

    @Override
    public String toString() {
        return name;
    }
}
