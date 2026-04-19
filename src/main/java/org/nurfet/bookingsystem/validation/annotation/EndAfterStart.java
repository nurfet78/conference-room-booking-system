package org.nurfet.bookingsystem.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.nurfet.bookingsystem.validation.validator.EndAfterStartValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EndAfterStartValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EndAfterStart {
    String message() default "End time must be after start time";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
