package network.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import network.dto.KafkaModel;
import network.enums.MessageStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class KafkaService {
    @Value("${kafka.topic.out}")
    private String topicOut;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final CounterService counterService;

    public KafkaService(KafkaTemplate<String, String> kafkaTemplate, CounterService counterService) {
        this.kafkaTemplate = kafkaTemplate;
        this.counterService = counterService;
        objectMapper = new ObjectMapper();
    }

    @KafkaListener(topics = "#{'${kafka.topic.in}'}")
    public void getMessage(@Payload String message,
                           @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                           @Header(KafkaHeaders.OFFSET) int offset) throws JsonProcessingException {
        log.info("Get new message partition [{}], offset [{}]", partition, offset);
            var info = (KafkaModel) objectMapper.readValue(message, KafkaModel.class);
            changeCount(MessageStatus.READ.equals(info.getStatus()), info.getUserId());
            try {
                kafkaTemplate.send(topicOut, UUID.randomUUID().toString(), objectMapper.writeValueAsString(info));
            } catch (Exception e) {
                changeCount(!MessageStatus.READ.equals(info.getStatus()), info.getUserId());
            }
    }

    private void changeCount(boolean isRead, String userId) {
        if(isRead)
            counterService.minusCount(userId);
        else
            counterService.addCount(userId);
    }
}
