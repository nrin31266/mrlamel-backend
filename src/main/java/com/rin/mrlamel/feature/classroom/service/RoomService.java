package com.rin.mrlamel.feature.classroom.service;

import com.rin.mrlamel.feature.classroom.dto.RoomDto;
import com.rin.mrlamel.feature.classroom.dto.req.CreateRoomReq;
import com.rin.mrlamel.feature.classroom.dto.req.UpdateRoomReq;

import java.util.List;

public interface RoomService {
    RoomDto createRoom(CreateRoomReq createRoomReq);
    RoomDto updateRoom(Long roomId, UpdateRoomReq updateRoomReq);
    RoomDto getRoomById(Long roomId);
    void deleteRoom(Long roomId);
    List<RoomDto> getAllRooms();
    List<RoomDto> getRoomsByBranchId(Long branchId);
}
