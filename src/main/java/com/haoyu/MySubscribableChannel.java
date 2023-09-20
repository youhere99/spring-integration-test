package com.haoyu;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MySubscribableChannel extends AbstractSubscribableChannel {

    private Random random = new Random();

    @Override
    protected boolean sendInternal(Message<?> message, long l) {
        if (message == null || CollectionUtils.isEmpty(getSubscribers())) {
            return false;
        }
        Iterator<MessageHandler> iterator = getSubscribers().iterator();

        int index = 0, targetIndex = random.nextInt(getSubscribers().size());
        while (iterator.hasNext()) {
            MessageHandler handler = iterator.next();
            if (index == targetIndex) {
                handler.handleMessage(message);
                return true;
            }
            index++;
        }

        return false;
    }

    public static void main(String[] args) {
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        MySubscribableChannel channel = new MySubscribableChannel();

        channel.addInterceptor(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                // 拦截header ignore=true的消息
                String ignoreKey = "ignore";
                if (message.getHeaders().containsKey(ignoreKey) && message.getHeaders().get(ignoreKey, Boolean.class)) {
                    return null;
                }

                return message;
            }

            @Override
            public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
                if (sent) {
                    successCount.incrementAndGet();
                } else {
                    failCount.incrementAndGet();
                }

            }

        });
        // 发送正常消息，因此时没有订阅，本次发送无任何反馈，send返回值为false
        channel.send(MessageBuilder
                .withPayload("costom payload1")
                .setHeader("k1", "v1")
                .build());
        // 创建三个订阅
        channel.subscribe(msg -> {
            System.out.println("[" + Thread.currentThread().getName() + "] handler1 receive: " + msg);
        });
        channel.subscribe(msg -> {
            System.out.println("[" + Thread.currentThread().getName() + "] handler2 receive: " + msg);
        });
        channel.subscribe(msg -> {
            System.out.println("[" + Thread.currentThread().getName() + "] handler3 receive: " + msg);
        });
        // 发送正常消息，会随机匹配一个订阅被消费
        channel.send(MessageBuilder
                .withPayload("costom payload2")
                .setHeader("k1", "v1")
                .build());
        // 发送带ignore的消息，会被忽略
        channel.send(MessageBuilder
                .withPayload("costom payload2")
                .setHeader("ignore", true)
                .build());

        System.out.println("successCount:" + successCount.get() + "," + "failCount:" + failCount.get());
    }

}
