public class Problem1 {

    public static void main(String[] args) {
        User sender = new User();
        User receiver = new User();
        sender.setPublicKey(123);
        receiver.setPublicKey(456);

        String originalMessage = "aaabbbbcccddd";
        Message message = new Message(sender, receiver, "", originalMessage);

        System.out.println("Original Message: " + message.getMessageBody());
        System.out.println("Original Message Metadata: " + message.getMetadata());

        // Compressing the message
        Message compressedMessage = compressMessage(message);
        System.out.println("Compressed Message: " + compressedMessage.getMessageBody());
        System.out.println("Compressed Message Metadata: " + compressedMessage.getMetadata());
    
        // Decompressing the message
        Message decompressedMessage = decompressMessage(compressedMessage);
        System.out.println("Decompressed Message: " + decompressedMessage.getMessageBody());
        System.out.println("Decompressed Message Metadata: " + decompressedMessage.getMetadata());
}

    private static String runLengthEncode(String input) {
        StringBuilder encoded = new StringBuilder();
        int count = 1;

        for (int i = 1; i < input.length(); i++) {
            if (input.charAt(i) == input.charAt(i-1)) {
                count++;
            }
            else {
                encoded.append(input.charAt(i-1)).append(count);
                count = 1;
            }
        }

        if (input.length() > 0) {
            encoded.append(input.charAt(input.length() - 1)).append(count);
        }

        return encoded.toString();
    }

    private static String runLengthDecode(String encoded) {
        StringBuilder decoded = new StringBuilder();
        int i = 0;

        while (i < encoded.length()) {
            char ch = encoded.charAt(i++);
            StringBuilder count = new StringBuilder();

            while (i < encoded.length() && Character.isDigit(encoded.charAt(i))) {
                count.append(encoded.charAt(i++));
            }
            decoded.append(String.valueOf(ch).repeat(Integer.parseInt(count.toString())));
        }
        return decoded.toString();
    }
    
    public static Message compressMessage(Message message) {
        String compressedBody = runLengthEncode(message.getMessageBody());
        return new Message(message.getSender(), message.getReceiver(), "Run-Length Encoded", compressedBody);
    }

    public static Message decompressMessage(Message message) {
        if (!"Run-Length Encoded".equals(message.getMetadata())) {
            throw new IllegalStateException("Message isn't run-length encoded.");
        }

        String decompressedBody = runLengthDecode(message.getMessageBody());
        return new Message(message.getSender(), message.getReceiver(), "", decompressedBody);
    }
}