package com.sai.tt_bot;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Component
@Slf4j
public class TTLoader {
    private final Config config;

    public TTLoader(Config config) {
        this.config = config;
    }

    private String convertWebPageToString(String ttUrl) throws Exception {
        return Jsoup.connect(ttUrl).get().html();
    }

    private String getDownloadUrl(String html) {
        String downloadUrl = html.split("\"downloadAddr\":\"")[1].split("\",\"shareCover\"")[0];
        downloadUrl = downloadUrl.replace("\\u002F", "/");
        downloadUrl = downloadUrl.replace("-prime", "");
        downloadUrl = downloadUrl.replace("v19", "v16");
        return downloadUrl;
    }

    private String downloadTikTokVideo(String videoUrl) throws IOException {
        String name = UUID.randomUUID() + ".mp4";
        String pathToFile = config.getPathToFolderForFiles() + "/" + name;

        FileUtils.copyURLToFile(
                new URL(videoUrl),
                new File(pathToFile),
                60000,
                60000);

        return pathToFile;
    }

    public String download(String url) throws Exception {
        String htmlPage = convertWebPageToString(url);
        String downloadUrl = getDownloadUrl(htmlPage);
        log.debug("downloadUrl: " + downloadUrl);
        return downloadTikTokVideo(downloadUrl);
    }
}
