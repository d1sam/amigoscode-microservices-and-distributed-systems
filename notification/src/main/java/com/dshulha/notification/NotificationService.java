package com.dshulha.notification;

import com.dshulha.clients.notification.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public void send(NotificationRequest notificationRequest) {
        notificationRepository.save(Notification.builder()
                .toCustomerId(notificationRequest.customerId())
                        .toCustomerEmail(notificationRequest.customerEmail())
                        .message(notificationRequest.message())
                        .sender("dshulha")
                        .sentAt(LocalDateTime.now())
                .build());
    }
}
