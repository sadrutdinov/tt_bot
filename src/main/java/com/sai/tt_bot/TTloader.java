package com.sai.tt_bot;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Component
@Slf4j
public class TTloader {
    private final Config config;

    public TTloader(Config config) {
        this.config = config;
    }

    private String convertWebPageToString(String ttUrl) throws Exception {
        return Jsoup.connect(ttUrl).get().html();
    }


    private Map<String, String> buildCookiesMap(String cookieStr) {
        Map<String, String> cookieMap = new HashMap<String, String>();
        String[] cookieArr = cookieStr.split("; ");
        Arrays.stream(cookieArr).forEach(cookie -> {
            String[] split = cookie.split("=");
            cookieMap.put(split[0], split[1]);
        });
        return cookieMap;
    }

    private String getDownloadUrl(String html) {
        String downloadUrl = html.split("\"downloadAddr\":\"")[1].split("\",\"shareCover\"")[0];
        downloadUrl = downloadUrl.replace("\\u002F", "/");
        return downloadUrl;
    }

    private String downloadTikTokVid(String vidUrl) {
        FileOutputStream out = null;

        String name = UUID.randomUUID().toString() + ".mp4";
        String pathToFile = config.getPathToFolderForFiles() + "/" + name;

        try {
            out = (new FileOutputStream(new File(pathToFile)));
            out.write(Jsoup.connect(vidUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36")
                    .cookies(buildCookiesMap(config.getCookiesStr()))
                    .ignoreContentType(true)
                    .execute()
                    .bodyAsBytes());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(out).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return pathToFile;

    }

    public String download(String url) throws Exception {
        String htmlPage = convertWebPageToString(url);
        String downloadUrl = getDownloadUrl(htmlPage);
        log.debug(downloadUrl);
        return downloadTikTokVid(downloadUrl);
    }
}
