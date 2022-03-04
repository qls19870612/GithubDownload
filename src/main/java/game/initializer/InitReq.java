package game.initializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InitReq {

    // 基础信息

    String desc() default "";

    String[] cfgKeys() default {};

    String cfgSplitSep() default "";

    String cfgPrefix() default "";

    Class<? extends Parser>[] parsers() default {};

    boolean skip() default false;

    // 验证信息

    boolean notNull() default true;

    boolean notEmpty() default false;

    long min() default 0;

    long max() default Integer.MAX_VALUE;

    int minLen() default 0;

    int maxLen() default Integer.MAX_VALUE;

    int minSize() default 0;

    int maxSize() default Integer.MAX_VALUE;

    // 扩展项

    String[] directives() default {};
}
