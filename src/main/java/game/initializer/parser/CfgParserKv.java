package game.initializer.parser;

import com.google.common.base.Splitter;

import java.util.List;

import game.initializer.Config;

import static com.google.common.base.Preconditions.checkArgument;

/** Created by wyt on 16-12-8. */
public class CfgParserKv {

    private static final Splitter SPLITTER_LF = Splitter.on('\n').trimResults();
    private static final Splitter SPLITTER_EQUAL = Splitter.on('=').trimResults().limit(2);
    private static final String[] COMMENT_MARKS = new String[]{"//"};
    private static final String[] COMMENT_MARKS1 = new String[]{"//", "#"};

    public static Config parse(String s, String name) {
        Config.Builder b = Config.builder(name, false);
        List<String> lines = SPLITTER_LF.splitToList(s);

        for (String line : lines) {
            boolean isComment = false;
            for (String s1 : COMMENT_MARKS1) {
                if (line.trim().startsWith(s1)) {
                    isComment = true;
                    continue;
                }
            }
            if (isComment) {
                continue;
            }

            line = trimComment(line);
            if (line.isEmpty()) {
                continue;
            }

            List<String> kv = SPLITTER_EQUAL.splitToList(line);
            checkArgument(kv.size() == 2, "配置行格式非法，合法格式为key=val！%s -> %s", name, line);
            b.add(kv.get(0), kv.get(1));
        }

        return b.build();
    }

    private static String trimComment(String line) {
        for (String commentMark : COMMENT_MARKS) {
            int i = line.indexOf(commentMark);
            if (i >= 0) {
                return line.substring(0, i);
            }
        }
        return line;
    }
}
