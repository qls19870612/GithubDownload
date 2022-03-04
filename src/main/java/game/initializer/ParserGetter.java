package game.initializer;

/** Created by wyt on 16-11-24. */
public interface ParserGetter {

    Parser get(Class<? extends Parser> cls);
}
