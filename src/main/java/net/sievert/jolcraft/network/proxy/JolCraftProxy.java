package net.sievert.jolcraft.network.proxy;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class JolCraftProxy {

    private static final Map<Class<?>, Object> CACHE = new ConcurrentHashMap<>();

    private JolCraftProxy() {}

    public static <T> T get(
            Class<T> apiType,
            String clientImplName,
            Supplier<? extends T> serverFactory
    ) {
        Object existing = CACHE.get(apiType);
        if (existing != null) {
            return apiType.cast(existing);
        }

        T created = create(apiType, clientImplName, serverFactory);

        Object prev = CACHE.putIfAbsent(apiType, created);
        return apiType.cast(prev != null ? prev : created);
    }

    private static <T> T create(
            Class<T> apiType,
            String clientImplName,
            Supplier<? extends T> serverFactory
    ) {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return serverFactory.get();
        }

        try {
            Class<?> raw = Class.forName(clientImplName, true, JolCraftProxy.class.getClassLoader());
            if (!apiType.isAssignableFrom(raw)) {
                return serverFactory.get();
            }

            @SuppressWarnings("unchecked")
            Class<? extends T> impl = (Class<? extends T>) raw;

            Constructor<? extends T> ctor = impl.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (ReflectiveOperationException | LinkageError e) {
            return serverFactory.get();
        }
    }

    private static final String CLIENT_ACCESS_IMPL = "net.sievert.jolcraft.network.proxy.JolCraftClientProxy";

    public static JolCraftClientAccess access() {
        return get(
                JolCraftClientAccess.class,
                CLIENT_ACCESS_IMPL,
                JolCraftServerProxy::new
        );
    }
}
