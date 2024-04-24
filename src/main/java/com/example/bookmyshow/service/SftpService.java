package com.example.bookmyshow.service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SftpService {

    private static final Logger logger = LoggerFactory.getLogger(SftpService.class);

    @Autowired
    private ChannelSftp channelSftp;

    public void uploadFile(String localFilePath, String remoteFilePath) {
        try {
            channelSftp.connect();
            channelSftp.put(localFilePath, remoteFilePath);
            logger.info("Upload Complete");
        } catch (SftpException | JSchException e) {
            logger.error("Error uploading file to SFTP", e);
        } finally {
            disconnectSftp();
        }
    }

    public void downloadFile(String localFilePath, String remoteFilePath) {
        try {
            channelSftp.connect();
            channelSftp.get(remoteFilePath, localFilePath);
            logger.info("Download Complete");
        } catch (SftpException | JSchException e) {
            logger.error("Error downloading file from SFTP", e);
        } finally {
            disconnectSftp();
        }
    }

    private void disconnectSftp() {
        if (channelSftp != null && channelSftp.isConnected()) {
            channelSftp.disconnect();
        }
    }
}
