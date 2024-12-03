public class Message {
    private User sender;
    private User receiver;
    private String metadata;
    private String messageBody;

    Message(User sender, User receiver, String metadata, String messageBody) {
        this.sender = sender;
        this.receiver = receiver;
        this.metadata = metadata;
        this.messageBody = messageBody;
    }
}
