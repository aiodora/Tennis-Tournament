package com.sd.tennis.dto;

import com.sd.tennis.validators.ValidTournamentDates;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ValidTournamentDates
public class TournamentDTO {
    @NotEmpty(message = "Tournament name cannot be empty")
    @Size(min = 3, max = 50, message = "Tournament name must be between 3 and 50 characters")
    private String name;

    @NotEmpty(message = "Tournament location cannot be empty")
    @Size(min = 3, max = 50, message = "Tournament location must be between 3 and 50 characters")
    private String location;

    @NotNull(message = "Tournament start date cannot be empty")
    private LocalDate startDate;

    @NotNull(message = "Tournament end date cannot be empty")
    private LocalDate endDate;

    @Size(max = 100, message = "Tournament description must be less than 100 characters")
    private String description;

    @Setter
    private String status = "Upcoming";;

    @NotNull(message = "Tournament registration deadline cannot be empty")
    private LocalDate registrationDeadline;
}
