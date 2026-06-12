package com.json.AutoAlquiler.utils;

import com.json.AutoAlquiler.services.ReservationAutomationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservationScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReservationScheduler.class);
    private final ReservationAutomationService automationService;

    public ReservationScheduler(ReservationAutomationService automationService) {
        this.automationService = automationService;
    }


    @Scheduled(cron = "0 0 * * * *")
    public void processExpiredReservations() {
        log.info("⏰ [Scheduler] Iniciando verificación de reservaciones expiradas en tiempo real...");
        
        try {
            int totalProcesadas = automationService.closeAndCleanExpiredReservations();
            if (totalProcesadas > 0) {
                log.info("✅ [Scheduler] Proceso completado. Se cerraron y depuraron {} reservaciones.", totalProcesadas);
            } else {
                log.info("💤 [Scheduler] Verificación finalizada. Sin registros expirados por depurar.");
            }
        } catch (Exception e) {
            log.error("🚨 [Scheduler] Error crítico durante la ejecución del cron automatizado, causado por: ", e);
        }
    }
}