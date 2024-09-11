package org.mansumugang.mansumugang_service.validation.location;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static org.mansumugang.mansumugang_service.constant.LocationBoundary.EXTREME_NORTH;
import static org.mansumugang.mansumugang_service.constant.LocationBoundary.EXTREME_SOUTH;

public class LatitudeValidator implements ConstraintValidator<Latitude, Double> {

    @Override
    public boolean isValid(Double latitude, ConstraintValidatorContext context) {
        if (latitude == null) {
            return true;
        }

        return EXTREME_SOUTH.getCoordinate() < latitude && latitude < EXTREME_NORTH.getCoordinate();
    }
}