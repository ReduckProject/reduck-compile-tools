package net.reduck.compile.tools;

import java.lang.annotation.*;

/**
 * @author Reduck
 * @since 2024/7/16 10:42
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface IgnoreCompile {
}
