package com.easybuy.UserService.Annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthorizeUser {
    String pathVariable() default "id";
}