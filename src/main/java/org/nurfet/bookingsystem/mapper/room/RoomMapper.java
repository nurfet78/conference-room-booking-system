package org.nurfet.bookingsystem.mapper.room;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.nurfet.bookingsystem.dto.request.CreateRoomRequest;
import org.nurfet.bookingsystem.dto.response.RoomResponse;
import org.nurfet.bookingsystem.entity.Room;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoomMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "active", ignore = true)
    Room toEntity(CreateRoomRequest request);

    RoomResponse toResponse(Room room);

    List<RoomResponse> toResponseList(List<Room> rooms);
}
