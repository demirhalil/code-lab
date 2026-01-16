package controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.sqs.SqsClient;

@RestController
public class MessageController {

    private final SqsClient sqsClient;

    @Value( "${aws.sqs.queue-url}")
    private String queueUrl;

    public MessageController(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @GetMapping("/send")
    public void sendMessage() {
        for (int i = 0; i < 1_000_000; i++) {
            String message = "Message " + i;
            System.out.println("Event is sending to queue: " + message);
            sqsClient
                    .sendMessage(builder -> builder.queueUrl(queueUrl).messageBody(message));
        }
    }
}
