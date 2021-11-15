package me.sunstorm.showmanager.http.routing;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.http.routing.annotate.Get;
import me.sunstorm.showmanager.http.routing.annotate.PathPrefix;
import me.sunstorm.showmanager.http.routing.annotate.Post;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

/**
 * Annotation based http routing system.
 */
@Slf4j
public class RoutingManager {
    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();
    private static final Predicate<Method> methodPredicate = m ->
            (m.isAnnotationPresent(Post.class) || m.isAnnotationPresent(Get.class))
            && m.getReturnType() == void.class
            && m.getParameterCount() == 1
            && Context.class.isAssignableFrom(m.getParameterTypes()[0]);

    /**
     * Creates the javalin routes.
     * @param javalin The javalin instance
     * @param handlers The handler classes
     */
    public static void create(@NotNull Javalin javalin, Class<?>... handlers) {
        log.info("Starting route creation");
        javalin.routes(() -> {
            for (Class<?> handlerClass : handlers) {
                try {
                    List<Method> methodCandidates = Arrays.stream(handlerClass.getDeclaredMethods()).filter(methodPredicate).collect(Collectors.toList());
                    if (methodCandidates.size() == 0) {
                        log.warn("Class {} does not contain eligible methods for routing", handlerClass.getName());
                        continue;
                    }
                    Object classInstance = handlerClass.getConstructor().newInstance();
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
                                    get(pathPrefix + method.getAnnotation(Get.class).value(), handlerInstance);
                                    break;
                                case POST:
                                    post(pathPrefix + method.getAnnotation(Post.class).value(), handlerInstance);
                                    break;
                            }
                        } catch (Throwable e) {
                            log.error("Failed to create routing for " + method.getName(), e);
                        }
                    }
                } catch (ReflectiveOperationException e) {
                    log.error("Couldn't access default constructor of class " + handlerClass.getName(), e);
                }
            }
        });
    }

    private static HandlerType getType(Method m) {
        for (HandlerType type : HandlerType.values()) {
            if (m.isAnnotationPresent(type.getAnnotationClass()))
                return type;
        }
        throw new IllegalStateException("invalid/missing type");
    }
}
