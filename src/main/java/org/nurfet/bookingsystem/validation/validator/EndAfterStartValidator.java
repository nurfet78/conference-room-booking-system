package org.nurfet.bookingsystem.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.nurfet.bookingsystem.validation.TimeRangeValidatable;
import org.nurfet.bookingsystem.validation.annotation.EndAfterStart;

import java.time.Instant;

public class EndAfterStartValidator
        implements ConstraintValidator<EndAfterStart, TimeRangeValidatable> {

    @Override
    public boolean isValid(TimeRangeValidatable value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Instant start = value.startTime();
        Instant end = value.endTime();

        // null-поля обрабатываются @NotNull на самих полях
        if (start == null || end == null) {
            return true;
        }

        boolean valid = end.isAfter(start);

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    context.getDefaultConstraintMessageTemplate()
            ).addPropertyNode("endTime").addConstraintViolation();
        }

        return valid;
    }
}
