package core.client;

import exceptions.InvalidProtocolException;
import exceptions.DownloadingException;

import java.io.IOException;

public interface Downloader {
    void downloadFile(int fileID) throws IOException, InvalidProtocolException, DownloadingException;
}
