package org.nurfet.bookingsystem.mapper.booking;

import jdk.jfr.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.nurfet.bookingsystem.dto.response.BookingResponse;
import org.nurfet.bookingsystem.entity.Booking;

import java.time.Duration;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {

    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomName", source = "room.name")
    @Mapping(target = "durationMinutes", source = ".",
             qualifiedByName = "calculateDuration")
    BookingResponse toResponse(Booking booking);

    List<BookingResponse> toResponseList(List<Booking> bookings);

    @Named("calculateDuration")
    default long calculateDuration(Booking booking) {
        return Duration.between(booking.getStartTime(), booking.getEndTime()).toMinutes();
    }
}