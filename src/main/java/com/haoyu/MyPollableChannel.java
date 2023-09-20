package com.haoyu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MyPollableChannel implements PollableChannel {
    private BlockingQueue<Message<?>> queue = new ArrayBlockingQueue<Message<?>>(1000);
    
    @Override
    public Message<?> receive() {
        return queue.poll();
    }

    @Override
    public Message<?> receive(long l) {
        try {
            return queue.poll(l, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.error("pollableChannel receive error", e);
        }
        return null;
    }

    @Override
    public boolean send(Message<?> message, long l) {
        return queue.add(message);
    }


    public static void main(String[] args) {
        MyPollableChannel channel = new MyPollableChannel();
        channel.send(MessageBuilder
                .withPayload("costom payload1")
                .setHeader("k1", "v1")
                .build());
        channel.send(MessageBuilder
                .withPayload("costom payload2")
                .setHeader("k2", "v2")
                .build());
        channel.send(MessageBuilder
                .withPayload("costom payload3")
                .setHeader("ingore", true)
                .build());
        System.out.println(channel.receive());
        System.out.println(channel.receive());
        System.out.println(channel.receive());
        System.out.println(channel.receive());
    }
}
