package gametest;

import net.sievert.jolcraft.JolCraft;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GameTestGroup {
    String path() default "";
    String namespace() default JolCraft.MOD_ID;
}