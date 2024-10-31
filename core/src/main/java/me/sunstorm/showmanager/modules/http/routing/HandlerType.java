package me.sunstorm.showmanager.modules.http.routing;

import me.sunstorm.showmanager.modules.http.routing.annotate.Get;
import me.sunstorm.showmanager.modules.http.routing.annotate.Post;

import java.lang.annotation.Annotation;

public enum HandlerType {
    GET(Get.class),
    POST(Post.class);

    private final Class<? extends Annotation> annotationClass;

    HandlerType(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }
}
