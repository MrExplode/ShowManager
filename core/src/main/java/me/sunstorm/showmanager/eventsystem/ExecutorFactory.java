package me.sunstorm.showmanager.eventsystem;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

public class ExecutorFactory implements Opcodes {
    private final String PACKAGE = "me.sunstorm.showmanager.eventsystem.generated";
    private final String SUPER_NAME = "java/lang/Object";
    private final String EXECUTE_DESC = "(Lme/sunstorm/showmanager/eventsystem/events/Event;Ljava/lang/Object;)V";
    private final String[] INTERFACE_NAME = new String[] {Type.getInternalName(EventExecutor.class)};
    private final String session = UUID.randomUUID().toString().substring(26);
    private final EventClassLoader classLoader;
    private final LoadingCache<Method, Class<? extends EventExecutor>> cache;

    public ExecutorFactory() {
        classLoader = new EventClassLoader(ExecutorFactory.class.getClassLoader());
        cache = CacheBuilder.newBuilder().weakValues().build(CacheLoader.from(method -> {
            String listenerName = Type.getInternalName(method.getDeclaringClass());
            Class<?> parameter = method.getParameterTypes()[0];
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            String className = String.format("%s.%s.%s-%s-%s", PACKAGE, session, method.getDeclaringClass().getSimpleName(), method.getName(), parameter.getSimpleName());
            cw.visit(V11, ACC_PUBLIC | ACC_FINAL, className.replace('.', '/'), null, SUPER_NAME, INTERFACE_NAME);
            MethodVisitor mv;
            {
                mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESPECIAL, SUPER_NAME, "<init>", "()V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }
            {
                mv = cw.visitMethod(ACC_PUBLIC, "execute", EXECUTE_DESC, null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 2);
                mv.visitTypeInsn(CHECKCAST, listenerName);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitTypeInsn(CHECKCAST, Type.getInternalName(parameter));
                mv.visitMethodInsn(INVOKEVIRTUAL, listenerName, method.getName(), Type.getMethodDescriptor(method), false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }
            cw.visitEnd();
            return classLoader.defineClass(className, cw.toByteArray());
        }));
    }

    public EventExecutor create(@NotNull Listener listener, Method method) throws InstantiationException, IllegalAccessException {
        if (!Modifier.isPublic(listener.getClass().getModifiers()))
            throw new IllegalArgumentException("Listener class must be public");
        if (!Modifier.isPublic(method.getModifiers()))
            throw new IllegalArgumentException("Event call method must be public");

        return cache.getUnchecked(method).newInstance();
    }

    private static final class EventClassLoader extends ClassLoader {
        public EventClassLoader(ClassLoader parent) {
            super(parent);
        }

        public <T> Class<T> defineClass(String name, byte[] bytes) {
            return (Class<T>) this.defineClass(name, bytes, 0, bytes.length);
        }
    }
}
