package com.work.hostel.config;

import com.work.hostel.repositories.ResidentRepo;
import com.work.hostel.services.PaymentService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private final PaymentService paymentService;
    private final ResidentRepo residentRepo;
    private static int lastProcessedDay = -1;

    // Миграция при старте приложения
    @PostConstruct
    public void migrateOnStartup() {
        log.info("Application started. Migrating old data...");
        
        // Миграция старых платежей
        int migratedPayments = paymentService.migrateOldPayments();
        if (migratedPayments > 0) {
            log.info("Migrated {} old payments on startup", migratedPayments);
        }
        
        // Миграция personCount для существующих жителей
        int migratedResidents = residentRepo.updateNullPersonCount(1);
        if (migratedResidents > 0) {
            log.info("Migrated {} residents with null personCount on startup", migratedResidents);
        }
    }

    // Проверяем каждый день в 00:01, является ли сегодня первым числом месяца
    @Scheduled(cron = "0 1 0 * * ?") // Каждый день в 00:01
    public void checkAndMigrateOnFirstDayOfMonth() {
        LocalDate today = LocalDate.now();
        int currentDay = today.getDayOfMonth();
        
        // Выполняем миграцию только один раз в первый день месяца
        if (currentDay == 1 && lastProcessedDay != 1) {
            log.info("First day of month detected. Migrating old payments...");
            int migrated = paymentService.migrateOldPayments();
            log.info("Migrated {} old payments", migrated);
            lastProcessedDay = 1;
        } else if (currentDay != 1) {
            lastProcessedDay = currentDay;
        }
    }
}

