package org.nurfet.bookingsystem.mapper.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.nurfet.bookingsystem.dto.response.BookingResponse;
import org.nurfet.bookingsystem.entity.Booking;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {

    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomName", source = "room.name")
    @Mapping(target = "durationMinutes",
            expression = "java(java.time.Duration.between(booking.getStartTime(), booking.getEndTime()).toMinutes())")
    BookingResponse toResponse(Booking booking);

    List<BookingResponse> toResponseList(List<Booking> bookings);
}
