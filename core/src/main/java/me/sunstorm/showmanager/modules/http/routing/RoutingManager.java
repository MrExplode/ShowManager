package me.sunstorm.showmanager.modules.http.routing;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import me.sunstorm.showmanager.modules.http.routing.annotate.Get;
import me.sunstorm.showmanager.modules.http.routing.annotate.PathPrefix;
import me.sunstorm.showmanager.modules.http.routing.annotate.Post;
import me.sunstorm.showmanager.util.Exceptions;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Annotation based http routing system.
 */
public class RoutingManager {
    private static final Logger log = LoggerFactory.getLogger(RoutingManager.class);

    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();
    private static final Predicate<Method> methodPredicate = m ->
            (m.isAnnotationPresent(Post.class) || m.isAnnotationPresent(Get.class))
            && m.getReturnType() == void.class
            && m.getParameterCount() == 1
            && Context.class.isAssignableFrom(m.getParameterTypes()[0]);

    /**
     * Creates the javalin routes.
     *
     * @param javalin The javalin instance
     * @param handlers The handler classes
     */
    public static void create(@NotNull Javalin javalin, Class<?>... handlers) {
        create(javalin, (c) -> {
            try {
                return c.getConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                Exceptions.sneaky(e);
                return null;
            }
        }, handlers);
    }

    /**
     * Creates the javalin routes. <br>
     * The instance factory allows the customization of instance creation, e.g. using dependency injection.
     *
     * @param javalin The javalin instance
     * @param instanceFactory The instance factory function
     * @param handlers The handler classes
     */
    public static void create(@NotNull Javalin javalin, Function<Class<?>, Object> instanceFactory, Class<?>... handlers) {
        log.info("Starting route creation");
        for (Class<?> handlerClass : handlers) {
            try {
                List<Method> methodCandidates = Arrays.stream(handlerClass.getDeclaredMethods()).filter(methodPredicate).toList();
                if (methodCandidates.isEmpty()) {
                    log.warn("Class {} does not contain eligible methods for routing", handlerClass.getName());
                    continue;
                }
                Object classInstance = instanceFactory.apply(handlerClass);
                String pathPrefix = "";
                if (handlerClass.isAnnotationPresent(PathPrefix.class))
                    pathPrefix = handlerClass.getAnnotation(PathPrefix.class).value();

                for (Method method : methodCandidates) {
                    try {
                        MethodHandle methodHandle = lookup.unreflect(method);
                        CallSite referenceSite = LambdaMetafactory.metafactory(
                                lookup,
                                "handle",
                                MethodType.methodType(Handler.class, handlerClass),
                                MethodType.methodType(void.class, Context.class),
                                methodHandle,
                                MethodType.methodType(void.class, Context.class)
                        );

                        Handler handlerInstance = (Handler) referenceSite.getTarget().bindTo(classInstance).invoke();
                        switch (getType(method)) {
                            case GET:
                                javalin.get(pathPrefix + method.getAnnotation(Get.class).value(), handlerInstance);
                                break;
                            case POST:
                                javalin.post(pathPrefix + method.getAnnotation(Post.class).value(), handlerInstance);
                                break;
                        }
                    } catch (Throwable e) {
                        log.error("Failed to create routing for {}", method.getName(), e);
                    }
                }
            } catch (Exception e) {
                log.error("Failed to instantiate handler: {}", handlerClass.getName(), e);
            }
        }
    }

    private static HandlerType getType(Method m) {
        for (HandlerType type : HandlerType.values()) {
            if (m.isAnnotationPresent(type.getAnnotationClass()))
                return type;
        }
        throw new IllegalStateException("invalid/missing type");
    }
}
