package com.json.AutoAlquiler.services;

import com.json.AutoAlquiler.models.ReservationHist;
import com.json.AutoAlquiler.models.User;
import com.json.AutoAlquiler.repositories.ReservationHistRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationHistService {

    private final ReservationHistRepository reservationHistRepository;

    @Transactional(readOnly = true)
    public List<ReservationHist> getReservationHistByOwner(User user) {
        try {
            if (user == null || user.getId() == null) {
                return Collections.emptyList();
            }

            boolean isSuperAdmin = user.getRole().getRoleName().toUpperCase().equalsIgnoreCase("ROLE_SUPER_ADMIN");

            if (isSuperAdmin) {
                return reservationHistRepository.findAllWithGraph();
            }

            return reservationHistRepository.findByClientIdOrderByVehicleIdAsc(user.getId());
        } catch (Exception e) {
            System.err.println(
                "🚨 Error en el servico al extraer el historial de reservaciones para el usuario en sesión ID [" +
                    (user != null ? user.getId() : "N/A") +
                    "]: " +
                    e.getMessage()
            );
            return Collections.emptyList();
        }
    }
}
