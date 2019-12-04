package service.middle;

import com.amazonaws.services.sqs.model.Message;
import com.google.gson.Gson;
import dao.SkierDaoImpl;
import entity.Skier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.GsonUtil;
import util.sqs.SQSUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class SQSQueueService implements IQueueService<Skier> {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    public static final int BATCH_SIZE = 10;

    private LinkedBlockingQueue<Skier> linkedBlockingQueue;

    public SQSQueueService() {
        this.linkedBlockingQueue = new LinkedBlockingQueue<>();
        startTimer();
    }
    @Override
    public void enqueue(Skier skier) {
        logger.info(GsonUtil.t2Json(skier));
        linkedBlockingQueue.add(skier);
    }

    private void startTimer() {
        for (int i = 0; i < 10; i ++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        List<Skier> skiers = dequeue(BATCH_SIZE);
                        if (skiers.size() == 0) {
                            try {
                                Thread.sleep(1000);
                                continue;
                            } catch (InterruptedException e) {
                            }
                        }
                        SQSUtil.sendMessageBatch(skiers.stream().map(GsonUtil::toJson).collect(Collectors.toList()));
                    }
                }
            }).start();
        }
    }

    @Override
    public List<Skier> dequeue(int num) {
        List<Skier> skiers = new ArrayList<>();
        for (int i = 0; i < num && !linkedBlockingQueue.isEmpty(); i++) {
            skiers.add(linkedBlockingQueue.remove());
        }
        return skiers;
    }
}
