package com.rin.mrlamel.feature.classroom.controller;

import com.rin.mrlamel.common.dto.response.ApiRes;
import com.rin.mrlamel.feature.classroom.dto.RoomDto;
import com.rin.mrlamel.feature.classroom.dto.req.CreateRoomReq;
import com.rin.mrlamel.feature.classroom.dto.req.UpdateRoomReq;
import com.rin.mrlamel.feature.classroom.model.ClassSession;
import com.rin.mrlamel.feature.classroom.model.Room;
import com.rin.mrlamel.feature.classroom.service.ClassService;
import com.rin.mrlamel.feature.classroom.service.RoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rooms")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class RoomController {
    RoomService roomService;
    ClassService classService;
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

    @GetMapping("/available")
    public ApiRes<List<Room>> getAvailableRoomsForSessions(
            @RequestParam(required = false) Long scheduleId,
            @RequestParam(required = false) Long sessionId,
            @RequestParam(required = false) Long clazzId,
            @RequestParam(name = "mode") String mode
    ) {
        if(mode.equals("by-clazz") && clazzId != null) {
            return ApiRes.success(roomService.getAvailableRoomsForSessions(classService.getClassSessionsByClassId(clazzId)));
        } else if(mode.equals("by-schedule") && scheduleId != null) {
            return ApiRes.success(roomService.getAvailableRoomsForSessions(classService.getClassSessionsByClassScheduleId(scheduleId)));
        } else if(mode.equals("by-session") && sessionId != null) {
            return ApiRes.success(roomService.getAvailableRoomsForSessions(List.of(classService.getClassSessionById(sessionId))));
        }
        return ApiRes.success(new ArrayList<>());
    }

    @PutMapping("/assignment")
    public ApiRes<Void> assignRoom(
            @RequestParam(required = false) Long scheduleId,
            @RequestParam(required = false) Long sessionId,
            @RequestParam(required = false) Long clazzId,
            @RequestParam(name = "mode") String mode,
            @RequestParam Long roomId
    ) {
        List<ClassSession> classSessions;
        if (mode.equals("by-clazz") && clazzId != null) {
            classSessions = classService.getClassSessionsByClassId(clazzId);
        } else if (mode.equals("by-schedule") && scheduleId != null) {
            classSessions = classService.getClassSessionsByClassScheduleId(scheduleId);
        } else if (mode.equals("by-session") && sessionId != null) {
            classSessions = List.of(classService.getClassSessionById(sessionId));
        } else {
            classSessions = new ArrayList<>();
        }
        roomService.assignRoomToSessions(roomId, classSessions);
        return ApiRes.success(null);
    }


}
