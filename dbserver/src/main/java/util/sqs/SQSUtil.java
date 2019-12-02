package util.sqs;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import com.amazonaws.services.sqs.model.Message;

import javax.jms.*;
import java.util.*;

public class SQSUtil {
    private static AmazonSQS sqs;

    static {
        AWSCredentials credentials = new AnonymousAWSCredentials();
        sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_WEST_2).build();
    }

    private SQSUtil() {
    }


    public static void sendMessage(String message) {
        //String queueUrl = sqs.getQueueUrl(queueName).getQueueUrl();
        SendMessageRequest request = new SendMessageRequest();
        request.withQueueUrl(SQSConfig.SQSUrl);
        request.withMessageBody(message);
        sqs.sendMessage(request);
    }


    public static List<Message> receiveMessages() {
        //String queueUrl = sqs.getQueueUrl(queueName).getQueueUrl();
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(SQSConfig.SQSUrl);
        receiveMessageRequest.setMaxNumberOfMessages(10);
        receiveMessageRequest.withWaitTimeSeconds(1);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        messages.forEach(message -> deleteMessage(message));
        return messages;
    }

    public static void deleteMessage(Message message) {
        sqs.deleteMessage(SQSConfig.SQSUrl, message.getReceiptHandle());
    }
}