package com.sai.tt_bot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    private final Config config;
    private TTloader tTloader;

    @Autowired
    public Bot(Config config, TTloader tTloader) {
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

            execute(sendVideo);

            if (file != null) {
                file.delete();
            }
        }

    }
}
