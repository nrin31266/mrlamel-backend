package com.rin.mrlamel.common.lifecycle;

import com.rin.mrlamel.feature.classroom.service.impl.ClassRoomLifecycleService;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LifecycleManager {
    ClassRoomLifecycleService classLifecycleService;
    @PostConstruct
    public void init() {
        // Initialize the lifecycle manager, e.g., start a scheduled task to update class statuses
        classLifecycleService.updateReadyClassesToOngoing();
        classLifecycleService.updateOngoingClassesToFinished();
    }

    @Scheduled(cron = "0 0 0 * * *") // Every day
    public void cronDaily() {
        // This method will be called every day at midnight to update class statuses
        classLifecycleService.updateReadyClassesToOngoing();
        classLifecycleService.updateOngoingClassesToFinished();
    }
}
