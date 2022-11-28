package com.sai.tt_bot;

import com.drew.imaging.mp4.Mp4MetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    private final Config config;
    private final TTLoader tTloader;

    @Autowired
    public Bot(Config config, TTLoader tTloader) {
        this.config = config;
        this.tTloader = tTloader;
    }

    @Override
    public String getBotUsername() {
        return config.getUserName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        log.debug(update.toString());
        String message = update.getMessage().getText();
        if (message.contains("tiktok.com/")) {

            log.debug(message);
            String filePath = tTloader.download(message);
            log.debug(filePath);
            Long chatId = update.getMessage().getChatId();

            InputFile video = new InputFile();
            File file = new File(filePath);
            video.setMedia(file);

            SendVideo sendVideo = new SendVideo();
            sendVideo.setChatId(String.valueOf(chatId));
            sendVideo.setVideo(video);
            sendVideo.setSupportsStreaming(true);
            setMetadata(file, sendVideo);
            execute(sendVideo);

            file.delete();
        }

    }

    private static void setMetadata(File file, SendVideo sendVideo) throws IOException {
        Metadata metadata = Mp4MetadataReader.readMetadata(file);
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                if (Objects.equals(tag.getTagName(), "Width")) {
                    sendVideo.setWidth(Integer.parseInt(tag.getDescription().replaceAll("\\D+", "")));
                }
                if (Objects.equals(tag.getTagName(), "Height")) {
                    sendVideo.setHeight(Integer.parseInt(tag.getDescription().replaceAll("\\D+", "")));
                }
            }
        }
    }

}
