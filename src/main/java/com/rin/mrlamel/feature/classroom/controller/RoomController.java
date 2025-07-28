package com.rin.mrlamel.feature.classroom.controller;

import com.rin.mrlamel.common.dto.response.ApiRes;
import com.rin.mrlamel.feature.classroom.dto.RoomDto;
import com.rin.mrlamel.feature.classroom.dto.req.CreateRoomReq;
import com.rin.mrlamel.feature.classroom.dto.req.UpdateRoomReq;
import com.rin.mrlamel.feature.classroom.service.RoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rooms")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class RoomController {
    RoomService roomService;
    // Define your endpoints here, e.g.:
    @GetMapping
    public ApiRes<List<RoomDto>> getAllRooms() {
        log.info("Fetching all rooms");
        var rooms = roomService.getAllRooms();
        return ApiRes.success(rooms);
    }
    @GetMapping("/branch/{branchId}")
    public ApiRes<List<RoomDto>> getRoomsByBranchId(@PathVariable Long branchId) {
        log.info("Fetching rooms for branch with ID: {}", branchId);
        var rooms = roomService.getRoomsByBranchId(branchId);
        return ApiRes.success(rooms);
    }
    @GetMapping("/{roomId}")
    public ApiRes<RoomDto> getRoomById(@PathVariable Long roomId) {
        log.info("Fetching room with ID: {}", roomId);
        var room = roomService.getRoomById(roomId);
        return ApiRes.success(room);
    }
    @PostMapping
    public ApiRes<RoomDto> createRoom(@RequestBody CreateRoomReq rq) {
        log.info("Creating new room: {}", rq);
        var createdRoom = roomService.createRoom(rq);
        return ApiRes.success(createdRoom);
    }
    @PutMapping("/{roomId}")
    public ApiRes<RoomDto> updateRoom(@PathVariable Long roomId, @RequestBody UpdateRoomReq rq) {
        log.info("Updating room with ID: {} with data: {}", roomId, rq);
        var updatedRoom = roomService.updateRoom(roomId, rq);
        return ApiRes.success(updatedRoom);
    }
    @DeleteMapping("/{roomId}")
    public ApiRes<Void> deleteRoom(@PathVariable Long roomId) {
        log.info("Deleting room with ID: {}", roomId);
        roomService.deleteRoom(roomId);
        return ApiRes.success(null);
    }

}
