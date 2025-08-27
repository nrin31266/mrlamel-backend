package com.rin.mrlamel.feature.classroom.service.impl;

import com.rin.mrlamel.common.constant.CLASS_STATUS;
import com.rin.mrlamel.feature.classroom.model.Clazz;
import com.rin.mrlamel.feature.classroom.repository.ClazzRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ClassRoomLifecycleService {
    private final ClazzRepository clazzRepository;

    @Transactional
    public void updateReadyClassesToOngoing() {
        LocalDate today = LocalDate.now();

        List<Clazz> readyClasses = clazzRepository.findClassesReadyToStart(today);

        for (Clazz clazz : readyClasses) {
            clazz.setStatus(CLASS_STATUS.ONGOING);
            if(clazz.getStartDate() == null) {
                clazz.setStartDate(today);
            }
        }

        clazzRepository.saveAll(readyClasses);
    }
    @Transactional
    public void updateOngoingClassesToFinished() {
        LocalDate today = LocalDate.now();

        List<Clazz> ongoingClasses = clazzRepository.findAllOngoingClassesEndedBefore(today, today.plusDays(1));

        for (Clazz clazz : ongoingClasses) {
            clazz.setStatus(CLASS_STATUS.FINISHED);
        }

        clazzRepository.saveAll(ongoingClasses);
    }

}
