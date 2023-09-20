package com.haoyu;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.stream.ByteStreamReadingMessageSource;
import org.springframework.integration.stream.ByteStreamWritingMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class SiBaseConfig {
//    public final String INPUT_DIR = "D:\\readfile\\in";
//    public final String OUTPUT_DIR = "D:\\readfile\\out";
//    public final String FILE_PATTERN = "*.txt";
//
//    @Bean(name = "fileChannel")
//    public MessageChannel fileChannel() {
//        return new DirectChannel();
//    }
//
//    @Bean
//    @InboundChannelAdapter(value = "fileChannel", poller = @Poller(fixedDelay = "1000"))
//    public MessageSource<File> fileReadingMessageSource() {
//        FileReadingMessageSource sourceReader = new FileReadingMessageSource();
//        sourceReader.setDirectory(new File(INPUT_DIR));
//        sourceReader.setFilter(new SimplePatternFileListFilter(FILE_PATTERN));
//        return sourceReader;
//    }
//
//    @Bean
//    @ServiceActivator(inputChannel = "fileChannel")
//    public MessageHandler fileWritingMessageHandler() {
//        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(OUTPUT_DIR));
//        handler.setFileExistsMode(FileExistsMode.REPLACE);
//        handler.setExpectReply(false);
//        return handler;
//    }

    @Bean(name = "stdChannel")
    public MessageChannel inputChannel() {
        return new DirectChannel();
    }

    @Bean
    @InboundChannelAdapter(value = "stdChannel", poller = @Poller(fixedDelay = "1000"))
    public MessageSource<?> stdOutMessageSource() {

        return new ByteStreamReadingMessageSource(System.in, 1024);
    }

    @Bean
    @ServiceActivator(inputChannel= "stdChannel")
    public MessageHandler stdInMessageSource() {

        return new ByteStreamWritingMessageHandler(System.out, 1024);
    }

}