package com.rin.mrlamel.feature.classroom.service.impl;

import com.rin.mrlamel.feature.classroom.dto.RoomDto;
import com.rin.mrlamel.feature.classroom.dto.req.CreateRoomReq;
import com.rin.mrlamel.feature.classroom.dto.req.UpdateRoomReq;
import com.rin.mrlamel.feature.classroom.mapper.RoomMapper;
import com.rin.mrlamel.feature.classroom.repository.RoomRepository;
import com.rin.mrlamel.feature.classroom.service.BranchService;
import com.rin.mrlamel.feature.classroom.service.RoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
