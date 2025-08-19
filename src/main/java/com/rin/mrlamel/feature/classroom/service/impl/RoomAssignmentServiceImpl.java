package com.rin.mrlamel.feature.classroom.service.impl;

import com.rin.mrlamel.feature.classroom.model.ClassSession;
import com.rin.mrlamel.feature.classroom.repository.ClassScheduleRepository;
import com.rin.mrlamel.feature.classroom.repository.ClazzRepository;
import com.rin.mrlamel.feature.classroom.repository.RoomRepository;
import com.rin.mrlamel.feature.classroom.service.ClassService;
import com.rin.mrlamel.feature.classroom.service.RoomAssignmentService;
import com.rin.mrlamel.feature.classroom.service.RoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomAssignmentServiceImpl implements RoomAssignmentService {

    private final ClassService classService;
    private final RoomService roomService;
    ClassScheduleRepository classScheduleRepository;
    ClazzRepository clazzRepository;
    RoomRepository roomRepository;


    @Override
    @Transactional
    public void assignRoomByClazz(Long clazzId, Long roomId) {
        if (clazzId == null) {
            throw new IllegalArgumentException("clazzId is required for mode=by-clazz");
        }
        List<ClassSession> sessions = classService.getClassSessionsByClassId(clazzId);
        roomService.assignRoomToSessions(roomId, sessions);
    }

    @Override
    @Transactional
    public void assignRoomBySchedule(Long scheduleId, Long roomId) {
        if (scheduleId == null) {
            throw new IllegalArgumentException("scheduleId is required for mode=by-schedule");
        }

        // Cập nhật tất cả sessions của schedule đó
        List<ClassSession> sessions = classService.getClassSessionsByClassScheduleId(scheduleId);
        roomService.assignRoomToSessions(roomId, sessions);
    }

    @Override
    @Transactional
    public void assignRoomBySession(Long sessionId, Long roomId) {
        if (sessionId == null) {
            throw new IllegalArgumentException("sessionId is required for mode=by-session");
        }
        ClassSession session = classService.getClassSessionById(sessionId);
        roomService.assignRoomToSessions(roomId, List.of(session));
    }
}
