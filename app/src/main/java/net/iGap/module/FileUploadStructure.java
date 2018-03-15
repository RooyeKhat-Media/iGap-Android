/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.module;

import net.iGap.proto.ProtoGlobal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * use this class structure to put selected files for uploading
 * update token when provided by server
 */
public class FileUploadStructure {
    public String fileName;
    public long fileSize;
    public String filePath;
    public byte[] fileHash;
    public String token;
    public FileChannel fileChannel;
    public RandomAccessFile randomAccessFile;
    public long messageId;
    public ProtoGlobal.RoomMessageType messageType;
    public long roomId;
    public String text;
    public long replyMessageId;

    public FileUploadStructure(String fileName, long fileSize, String filePath, long messageId) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.messageId = messageId;
    }

    public FileUploadStructure(String fileName, long fileSize, String filePath, long messageId, ProtoGlobal.RoomMessageType messageType, long roomId, long replyMessageId) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.messageId = messageId;
        this.messageType = messageType;
        this.roomId = roomId;
        this.replyMessageId = replyMessageId;
    }

    public void setFileHash(byte[] fileHash) {
        this.fileHash = fileHash;
    }

    public void openFile(String filePath) throws FileNotFoundException {
        if (randomAccessFile == null) {
            randomAccessFile = new RandomAccessFile(new File(filePath), "r");
        }
        if (fileChannel == null || !fileChannel.isOpen()) {
            fileChannel = randomAccessFile.getChannel();
        }
    }

    public void closeFile() throws IOException {
        try {
            if (fileChannel != null) {
                fileChannel.close();
            }
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        } finally {
            fileChannel = null;
            randomAccessFile = null;
        }
    }
}