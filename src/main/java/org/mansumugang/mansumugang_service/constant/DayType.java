package org.mansumugang.mansumugang_service.constant;

import lombok.Getter;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;

@Getter
public enum DayType {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    public static DayType getDayType(String dayTypeStr) throws CustomErrorException {
        for (DayType dayType : DayType.values()) {
            if(dayType.name().equals(dayTypeStr)){
                return dayType;
            }
        }

        throw new CustomErrorException(ErrorType.NoSuchDayTypeError);
    }
}