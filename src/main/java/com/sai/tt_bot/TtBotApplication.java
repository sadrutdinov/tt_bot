package com.sai.tt_bot;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class TtBotApplication {

    private final Bot bot;

    public TtBotApplication(Bot bot) {
        this.bot = bot;
    }

    @PostConstruct
    public void init() {
        {
            try {
                TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
                telegramBotsApi.registerBot(bot);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(TtBotApplication.class);
    }

}
