package com.sd.tennis.specification;

import com.sd.tennis.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.Period;

public class PlayerSpecification {
    public static Specification<User> hasRole(String role) {
        return (root, query, cb) -> cb.equal(root.get("role"), role);
    }

    public static Specification<User> minRanking(Integer min) {
        return (root, query, cb) ->
                min == null ? null : cb.greaterThanOrEqualTo(root.get("ranking"), min);
    }

    public static Specification<User> maxRanking(Integer max) {
        return (root, query, cb) ->
                max == null ? null : cb.lessThanOrEqualTo(root.get("ranking"), max);
    }

    public static Specification<User> nationalityEquals(String nat) {
        return (root, query, cb) ->
                nat == null ? null : cb.equal(root.get("nationality"), nat);
    }

    public static Specification<User> minAge(Integer age) {
        return (root, query, cb) -> {
            if (age == null) return null;
            LocalDate cutoff = LocalDate.now().minusYears(age);
            return cb.lessThanOrEqualTo(root.get("birthDate"), cutoff);
        };
    }

    public static Specification<User> maxAge(Integer age) {
        return (root, query, cb) -> {
            if (age == null) return null;
            LocalDate cutoff = LocalDate.now().minusYears(age);
            return cb.greaterThanOrEqualTo(root.get("birthDate"), cutoff);
        };
    }
}
