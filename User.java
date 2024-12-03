public class User {
    private int privateKey;
    private int publicKey;
    private int n;

    public int getPrivateKey() {
        return privateKey;
    }

    public int getPublicKey() {
        return publicKey;
    }

    public int getN() {
        return n;
    }

    public void setPrivateKey(int privateKey) {
        this.privateKey = privateKey;
    }

    public void setPublicKey(int publicKey) {
        this.publicKey = publicKey;
    }

    public void setN(int n) {
        this.n = n;
    }
}
