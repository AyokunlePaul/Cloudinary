package i.am.eipeks.cloudinary;


public class Message {
    private String messageType, messageContent, messageTime, user;

    public Message(String messageType, String messageContent, String messageTime, String user) {
        this.messageType = messageType;
        this.messageContent = messageContent;
        this.messageTime = messageTime;
        this.user = user;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
