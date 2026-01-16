package listener;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;

@Service
public class SqsMessageListener {
    private final SqsClient sqsClient;

    public SqsMessageListener(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    @PostConstruct
    public void listen() {
        sqsClient.receiveMessage(builder -> builder.queueUrl(queueUrl));
    }
}
