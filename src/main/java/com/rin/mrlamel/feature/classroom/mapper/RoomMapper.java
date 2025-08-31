package com.rin.mrlamel.feature.classroom.mapper;

import com.rin.mrlamel.feature.classroom.dto.RoomDto;
import com.rin.mrlamel.feature.classroom.dto.req.CreateRoomReq;
import com.rin.mrlamel.feature.classroom.dto.req.UpdateRoomReq;
import com.rin.mrlamel.feature.classroom.model.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    RoomDto toRoomDto(Room room);
    Room toRoom(CreateRoomReq createRoomReq);
    void updateRoom(UpdateRoomReq createRoomReq,@MappingTarget Room room);
}