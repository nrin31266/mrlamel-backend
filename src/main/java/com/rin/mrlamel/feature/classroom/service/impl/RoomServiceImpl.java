package com.rin.mrlamel.feature.classroom.service.impl;

import com.rin.mrlamel.feature.classroom.dto.RoomDto;
import com.rin.mrlamel.feature.classroom.dto.req.CreateRoomReq;
import com.rin.mrlamel.feature.classroom.dto.req.UpdateRoomReq;
import com.rin.mrlamel.feature.classroom.mapper.RoomMapper;
import com.rin.mrlamel.feature.classroom.model.ClassSession;
import com.rin.mrlamel.feature.classroom.model.Room;
import com.rin.mrlamel.feature.classroom.repository.RoomRepository;
import com.rin.mrlamel.feature.classroom.service.BranchService;
import com.rin.mrlamel.feature.classroom.service.RoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RoomServiceImpl implements RoomService {
    RoomRepository roomRepository;
    RoomMapper roomMapper;
    BranchService branchService;
    @Override
    public RoomDto createRoom(CreateRoomReq createRoomReq) {
        // Check if a room with the same code already exists
        if (roomRepository.existsByCode(createRoomReq.getCode())) {
            throw new IllegalArgumentException("Room with code " + createRoomReq.getCode() + " already exists.");
        }

        // Map CreateRoomReq to Room entity
        var room = roomMapper.toRoom(createRoomReq);
        // Set the branch entity if branchId is provided
        var branch = branchService.getBranchById(createRoomReq.getBranchId());
        room.setBranch(branch);
        // Save the room entity to the repository
        var savedRoom = roomRepository.save(room);

        // Convert the saved Room entity to RoomDto and return it
        return roomMapper.toRoomDto(savedRoom);
    }

    @Override
    public RoomDto updateRoom(Long roomId, UpdateRoomReq updateRoomReq) {
        // Find the room by ID
        var room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomId));

        // Update the room entity with the provided data
        roomMapper.updateRoom(updateRoomReq, room);
        var branch = branchService.getBranchById(updateRoomReq.getBranchId());
        room.setBranch(branch);
        // Save the updated room entity to the repository
        var updatedRoom = roomRepository.save(room);

        // Convert the updated Room entity to RoomDto and return it
        return roomMapper.toRoomDto(updatedRoom);
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        // Find the room by ID
        var room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomId));

        // Convert the Room entity to RoomDto and return it
        return roomMapper.toRoomDto(room);
    }

    @Override
    public void deleteRoom(Long roomId) {
        // Check if the room exists before attempting to delete
        if (!roomRepository.existsById(roomId)) {
            throw new IllegalArgumentException("Room not found with id: " + roomId);
        }

        // Delete the room by ID
        roomRepository.deleteById(roomId);
    }

    @Override
    public List<RoomDto> getAllRooms() {
        // Fetch all rooms from the repository
        var rooms = roomRepository.findAll();

        // Convert the list of Room entities to a list of RoomDto
        return rooms.stream()
                .map(roomMapper::toRoomDto)
                .toList();
    }

    @Override
    public List<RoomDto> getRoomsByBranchId(Long branchId) {
    // Fetch all rooms associated with the given branch ID
        var rooms = roomRepository.findByBranchId(branchId);
        // Convert the list of Room entities to a list of RoomDto
        return rooms.stream()
                .map(roomMapper::toRoomDto)
                .toList();
    }

    @Override
    public List<Room> getAvailableRoomsForSessions(List<ClassSession> sessions) {
        if (sessions.isEmpty()) return Collections.emptyList();

        Set<Room> available = new HashSet<>(roomRepository.getAvailableRoomForSession(
                sessions.getFirst().getDate(),
                sessions.getFirst().getStartTime(),
                sessions.getFirst().getEndTime(),
                sessions.getFirst().getId() != null ? sessions.getFirst().getId() : 0L
        ));

        for (int i = 1; i < sessions.size(); i++) {
            if (available.isEmpty()) break; // Nếu không còn phòng nào rảnh thì dừng
            ClassSession session = sessions.get(i);
            List<Room> freeForSession = roomRepository.getAvailableRoomForSession(
                    session.getDate(),
                    session.getStartTime(),
                    session.getEndTime(),
                    session.getId() != null ? session.getId() : 0L
            );
            available.retainAll(freeForSession);
        }

        return new ArrayList<>(available);

    }

    @Override
    public boolean isRoomAvailableForAllSessions(Long roomId, List<ClassSession> sessions) {
        for (ClassSession session : sessions) {
            long conflicts = roomRepository.countConflictingSessions(
                    roomId,
                    session.getDate(),
                    session.getStartTime(),
                    session.getEndTime(),
                    session.getId() != null ? session.getId() : 0L
            );
            if (conflicts > 0) {
                return false; // Room bị trùng với session khác
            }
        }
        return true; // Room trống cho tất cả session
    }

    @Override
    public void assignRoomToSessions(Long roomId, List<ClassSession> classSessions) {
        // Kiểm tra xem roomId có hợp lệ không
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomId));
        // Kiểm tra xem phòng có khả dụng cho tất cả session không
        if (!isRoomAvailableForAllSessions(roomId, classSessions)) {
            throw new IllegalArgumentException("Room with ID " + roomId + " is not available for all provided sessions.");
        }
        LocalDateTime now = LocalDateTime.now();
        // Gán phòng cho từng session
        for (ClassSession session : classSessions) {
//            if(session.getDate().isBefore(now.toLocalDate()) ||
//               (session.getDate().isEqual(now.toLocalDate()) && session.getStartTime().isBefore(now.toLocalTime()))) {
//                continue;
//            }
            session.setRoom(room);
        }

        // Them các session vào phòng
        room.getSessions().addAll(classSessions); // Thêm các session vào phòng

        // Lưu tất cả session đã cập nhật
        roomRepository.save(room);
    }
}
