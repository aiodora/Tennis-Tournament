package com.sd.tennis.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidTournamentDatesImpl.class)
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface ValidTournamentDates {
    String message() default "Invalid tournament dates";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
