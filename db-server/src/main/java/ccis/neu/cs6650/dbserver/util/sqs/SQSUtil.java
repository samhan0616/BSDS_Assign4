package ccis.neu.cs6650.dbserver.util.sqs;

import ccis.neu.cs6650.dbserver.util.GsonUtil;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;


import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SQSUtil {
    private static AmazonSQS sqs;

    static {
        AWSCredentials credentials = new AnonymousAWSCredentials();
        sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_WEST_1).build();
    }

    private SQSUtil() {
    }


    public static void sendMessage(String message) {

        SendMessageRequest request = new SendMessageRequest();
        request.withQueueUrl(SQSConfig.SQSUrl);
        request.withMessageBody(message);
        sqs.sendMessage(request);
    }

    public static void sendMessageBatch(List<String> message) {

        SendMessageBatchRequest request = new SendMessageBatchRequest();
        request.withQueueUrl(SQSConfig.SQSUrl);
        request.withEntries(message.stream().map(msg -> {
            String body = GsonUtil.toJson(msg);
            SendMessageBatchRequestEntry entry = new SendMessageBatchRequestEntry();
            entry.setMessageBody(body);
            entry.setId(UUID.randomUUID().toString());
            return entry;
        }).collect(Collectors.toList()));
        sqs.sendMessageBatch(request);
    }


    public static List<Message> receiveMessages() {
        //String queueUrl = sqs.getQueueUrl(queueName).getQueueUrl();
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(SQSConfig.SQSUrl);
        receiveMessageRequest.setMaxNumberOfMessages(10);
        receiveMessageRequest.setVisibilityTimeout(60);
//        receiveMessageRequest.withWaitTimeSeconds(1);
        return sqs.receiveMessage(receiveMessageRequest).getMessages();
    }

    public static void deleteMessageBatch(List<Message> message) {
        DeleteMessageBatchRequest request = new DeleteMessageBatchRequest();
        request.setQueueUrl(SQSConfig.SQSUrl);
        request.withEntries(message.stream().map(msg -> {
            DeleteMessageBatchRequestEntry entry = new DeleteMessageBatchRequestEntry();
            entry.setId(UUID.randomUUID().toString());
            entry.setReceiptHandle(msg.getReceiptHandle());
            return entry;
        }).collect(Collectors.toList()));
        sqs.deleteMessageBatch(request);
    }

    public static void deleteMessage(Message message) {
        sqs.deleteMessage(SQSConfig.SQSUrl, message.getReceiptHandle());
    }


    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, 0, 15,9,0);
        System.out.println(calendar.getTimeInMillis());
        calendar.set(2019, 0, 15,15,0);
        System.out.println(calendar.getTimeInMillis());
    }

}