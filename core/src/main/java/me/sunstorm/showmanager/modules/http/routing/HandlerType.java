package me.sunstorm.showmanager.modules.http.routing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sunstorm.showmanager.modules.http.routing.annotate.Get;
import me.sunstorm.showmanager.modules.http.routing.annotate.Post;

import java.lang.annotation.Annotation;

@Getter
@AllArgsConstructor
public enum HandlerType {
    GET(Get.class),
    POST(Post.class);

    private final Class<? extends Annotation> annotationClass;
}
