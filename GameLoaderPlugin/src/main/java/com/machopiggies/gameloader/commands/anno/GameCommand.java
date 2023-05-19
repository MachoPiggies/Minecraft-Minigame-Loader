package com.machopiggies.gameloader.commands.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GameCommand {
    String name();
    String description();
    String[] aliases() default {};
    String usage() default "";
    String permission() default "";
}
