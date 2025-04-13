package com.sd.tennis.validators;

import com.sd.tennis.dto.TournamentDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ValidTournamentDatesImpl implements ConstraintValidator<ValidTournamentDates, TournamentDTO> {
    @Override
    public boolean isValid(TournamentDTO dto, ConstraintValidatorContext context) {
        try {
            LocalDate start = dto.getStartDate();
            LocalDate end = dto.getEndDate();

            if (start == null || end == null) {
                return false;
            }

            return !start.isAfter(end);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
