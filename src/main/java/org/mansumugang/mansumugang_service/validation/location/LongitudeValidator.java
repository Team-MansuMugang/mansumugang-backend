package org.mansumugang.mansumugang_service.validation.location;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static org.mansumugang.mansumugang_service.constant.LocationBoundary.*;
import static org.mansumugang.mansumugang_service.constant.LocationBoundary.EXTREME_EAST;

public class LongitudeValidator implements ConstraintValidator<Longitude, Double> {

    @Override
    public boolean isValid(Double longitude, ConstraintValidatorContext context) {
        if (longitude == null) {
            return true;
        }

        return EXTREME_WEST.getCoordinate() < longitude && longitude < EXTREME_EAST.getCoordinate();
    }
}