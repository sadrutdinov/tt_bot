package com.sai.tt_bot;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Config {
    @Value("${token}")
    private String token;
    @Value("${userName}")
    private String userName;
    @Value("${downloadFolder}")
    private String pathToFolderForFiles;

}
