package net.iGap.module.structs;

public class DataUsageStruct {

    int viewType;
    long byteReceived;
    long byteSend;
    int sendNum;
    int receivednum;
    String title;


    public DataUsageStruct(int viewType, long byteReceived, long byteSend, int sendNum, int receivednum, String title) {
        this.viewType = viewType;
        this.byteReceived = byteReceived;
        this.byteSend = byteSend;
        this.sendNum = sendNum;
        this.receivednum = receivednum;
        this.title = title;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public long getByteReceived() {
        return byteReceived;
    }

    public void setByteReceived(long byteReceived) {
        this.byteReceived = byteReceived;
    }

    public long getByteSend() {
        return byteSend;
    }

    public void setByteSend(long byteSend) {
        this.byteSend = byteSend;
    }

    public int getSendNum() {
        return sendNum;
    }

    public void setSendNum(int sendNum) {
        this.sendNum = sendNum;
    }

    public int getReceivednum() {
        return receivednum;
    }

    public void setReceivednum(int receivednum) {
        this.receivednum = receivednum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
