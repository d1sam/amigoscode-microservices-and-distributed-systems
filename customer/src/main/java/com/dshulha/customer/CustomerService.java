package com.dshulha.customer;

import com.dshulha.amqp.RabbitMQMessageProducer;
import com.dshulha.clients.fraud.FraudCheckResponse;
import com.dshulha.clients.fraud.FraudClient;
import com.dshulha.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final RabbitMQMessageProducer producer;

    public void registerCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        Customer customer = Customer.builder()
                .firstName(customerRegistrationRequest.firstName())
                .lastName(customerRegistrationRequest.lastName())
                .email(customerRegistrationRequest.email())
                .build();
        customerRepository.saveAndFlush(customer);
        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());
        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }
        NotificationRequest notificationRequest = new NotificationRequest(customer.getId(), customer.getEmail(),
                String.format("Hi %s, welcome to dshulha", customer.getFirstName()));

        producer.publish(notificationRequest, "internal.exchange", "internal.notification.routing-key");
    }
}
