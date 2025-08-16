package com.rin.mrlamel.feature.classroom.service;

public interface RoomAssignmentService {
    void assignRoomByClazz(Long clazzId, Long roomId);
    void assignRoomBySchedule(Long scheduleId, Long roomId);
    void assignRoomBySession(Long sessionId, Long roomId);
}
