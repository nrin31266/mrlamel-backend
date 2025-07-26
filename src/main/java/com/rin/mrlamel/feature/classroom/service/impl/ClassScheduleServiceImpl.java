package com.rin.mrlamel.feature.classroom.service.impl;

import com.rin.mrlamel.feature.classroom.repository.ClassScheduleRepository;
import com.rin.mrlamel.feature.classroom.service.ClassScheduleService;
import com.rin.mrlamel.feature.classroom.service.ClassSessionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ClassScheduleServiceImpl implements ClassScheduleService {
    ClassScheduleRepository classScheduleRepository;
}
