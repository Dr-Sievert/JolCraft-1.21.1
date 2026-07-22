package gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.level.block.Rotation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JolCraftTestFunction {

    public static Collection<TestFunction> getTestsFrom(Class<?>... classes) {
        return Stream.of(classes)
                .map(Class::getDeclaredMethods)
                .flatMap(Arrays::stream)
                .map(JolCraftTestFunction::of)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static TestFunction of(Method method) {
        GameTest gt = method.getAnnotation(GameTest.class);
        if (gt == null) return null;
        if (!Modifier.isStatic(method.getModifiers()))
            throw new IllegalArgumentException("Test must be static: " + method.getName());
        if (method.getParameterCount() != 1 || !GameTestHelper.class.isAssignableFrom(method.getParameterTypes()[0]))
            throw new IllegalArgumentException("Test must take GameTestHelper: " + method.getName());

        Class<?> owner = method.getDeclaringClass();
        GameTestGroup group = owner.getAnnotation(GameTestGroup.class);
        if (group == null)
            throw new IllegalArgumentException(owner.getName() + " must be annotated with @gametest.GameTestGroup");

        String base = "%s:gametest".formatted(group.namespace());

        String structure = group.path().isEmpty()
                ? "%s/%s".formatted(base, gt.template())
                : "%s/%s/%s".formatted(base, group.path(), gt.template());

        return new TestFunction(
                gt.batch(),
                owner.getSimpleName() + "." + method.getName(),
                structure,
                Rotation.NONE,
                gt.timeoutTicks(),
                gt.setupTicks(),
                gt.required(),
                helper -> {
                    try {
                        method.invoke(null, helper);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}
