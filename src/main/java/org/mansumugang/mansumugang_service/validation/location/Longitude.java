package org.mansumugang.mansumugang_service.validation.location;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = LongitudeValidator.class)
@Documented
public @interface Longitude {

    String message() default "대한민국 영역의 경도가 아닙니다.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}