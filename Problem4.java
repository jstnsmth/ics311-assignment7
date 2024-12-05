public static int computeHash(String messageBody) {
    return messageBody.hashCode();
}
public static int signMessage(String messageBody, User sender) {
    int hash = computeHash(messageBody);
    // encrypt hash using private key
    return Problem3.modPow(hash, sender.getPrivateKey(), sender.getN());
}

public static boolean verifySignature(Message message, User sender) {
    int hash = computeHash(message.getMessageBody());
    // decript signature using the sender's public key
    int decryptedHash = Problem3.modPow(Integer.parseInt(message.getMetadata()), sender.getPublicKey(), sender.getN());
    return hash == decryptedHash;
}

public static Message createSignedMessage(Message originalMessage) {
    int signature = signMessage(originalMessage.getMessageBody(), originalMessage.getSender());
    return new Message(
        originalMessage.getSender(),
        originalMessage.getReceiver(),
        String.valueOf(signature), // store sig in metadata
        originalMessage.getMessageBody()
    );
}

public class Problem4 {

    public static void main(String[] args) {
        // creating users
        User Alice = new User();
        User Bob = new User();

        // rsa keys for users
        Problem3.sieveOfEratosthenes();
        Problem3.chooseKeys(Alice);
        Problem3.chooseKeys(Bob);

        // message from alice to bob
        Message message = new Message(Alice, Bob, null, "This is a secure message!");

        // signature
        Message signedMessage = createSignedMessage(message);
        System.out.println("Signed Message Metadata (Signature): " + signedMessage.getMetadata());

        // verify authenticity and integrity 
        boolean isValid = verifySignature(signedMessage, Alice);
        System.out.println("Is the signature valid? " + isValid);
    }
}
