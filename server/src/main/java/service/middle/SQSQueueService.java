package service.middle;

import com.amazonaws.services.sqs.model.Message;
import entity.Skier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.GsonUtil;
import util.sqs.SQSUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SQSQueueService implements IQueueService<Skier> {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void enqueue(Skier skier) {
        logger.info(GsonUtil.t2Json(skier));
        String g = GsonUtil.t2Json(skier);
        SQSUtil.sendMessage(g);
    }

    @Override
    public List<Skier> dequeue(int num) {
        List<Message> msgs = SQSUtil.receiveMessages();
       return  msgs.stream().map(msg -> GsonUtil.fromJson(msg.getBody(), Skier.class)).collect(Collectors.toList());
    }
}