import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class Problem3 {
    public static HashSet<Integer> prime = new HashSet<>();
    public static Random random = new Random();
    public static Integer n = null;
    public static Integer publicKey = null;
    public static Integer privateKey = null;
    public static void main(String[] args) {
        /**sieveOfEratosthenes();
        for (Integer primeNum : prime) {
            System.out.println(primeNum);
        }
        System.out.println("size: " + prime.size());*/
        User Michael = new User();
        User John = new User();
        Message message1 = new Message(Michael, John, null, "Text Message");

        sieveOfEratosthenes();
        chooseKeys(Michael);
        chooseKeys(John);
        String message = "Text Message";
        List<Integer> coded = encoder(message1.getMessageBody(), message1.getSender(), message1.getReceiver());
        System.out.println("Initial message");
        System.out.println(message1.getMessageBody());
        System.out.println("The encoded message");
        System.out.println(String.join("", coded.stream().map(Object::toString).toArray(String[] :: new)));
        System.out.println("Decoded message");
        System.out.println(decoder(coded, message1.getSender(), message1.getReceiver()));
    }

    // Computes Prime numbers from 1-300;
    public static void sieveOfEratosthenes() {
        boolean[] sieve = new boolean[300];
        for (int i = 0; i < 300; i++) {
            sieve[i] = true;
        }

        sieve[0] = false;
        sieve[1] = false;

        for (int i = 2; i < 300; i++) {
            if (sieve[i] && i * i < 300) {
                for (int j = i * i; j < 300; j += i) {
                    sieve[j] = false;
                }
            }
        }

        for (int i = 0; i < sieve.length; i++) {
            if (sieve[i]) {
                prime.add(i);
            }
        }


    }
    // Randomly picks number from the list of prime numbers
    public static int pickPrimeNumber(){
        int k = random.nextInt(prime.size());
        List<Integer> copyOfPrimeNumbers = new ArrayList<>(prime);
        int primeNum = copyOfPrimeNumbers.get(k);
        // Removes the prime number from hash set so p != q
        prime.remove(primeNum);
        return primeNum;
    }

    public static int[] extendedEuclid(int a,int b) {
        if (b == 0) {
            return new int[]{a,1,0};
        }
        else {
            int[] values = extendedEuclid(b, a % b);
            int d1 = values[0];
            int x1 = values[1];
            int y1 = values[2];

            int d = d1;
            int x = y1;
            int y = x1 - (Math.floorDiv(a, b)) * y1;

            return new int[]{d,x,y};
        }
    }

    public static int modLinearEquationSolver(int a, int b, int n) {
        int[] values = extendedEuclid(a,n);
        int d = values[0];
        int x1 = values[1];
        int y1 = values[2];
        int solution = 0;
        if (d % b == 0) {
            int x = ((x1) * (b/d)) % n;
            for(int i = 0; i <= d-1; i++) {
                solution = (x + i * (n/d)) % n;
            }
        }
        return solution;
    }

    public static void chooseKeys(User user) {
        int prime1 = pickPrimeNumber();
        int prime2 = pickPrimeNumber();

        n = prime1 * prime2;
        int phi = (prime1 - 1) * (prime2 -1);
        int e = 2;
        while (true) {
            if (modLinearEquationSolver(e, 1, phi) == 1) {
                break;
            }
            e += 1;
        }
        publicKey = e;
        privateKey = modLinearEquationSolver(e, 1, phi);
        user.setPrivateKey(privateKey);
        user.setPublicKey(publicKey);
    }

    public static int encrypt(int message, User sender, User receiver ) {
        //int e = publicKey;
        int e = sender.getPrivateKey();
        int encryptedText = 1;
        while (e > 0) {
            encryptedText *= message;
            encryptedText %= n;
            e -= 1;
        }

        e = receiver.getPublicKey();
        while (e > 0) {
            encryptedText *= message;
            encryptedText %= n;
            e -= 1;
        }

        return encryptedText;
    }

    public static int decrypt( int encryptedText, User sender, User receiver) {
      // int d = privateKey;
      int d = receiver.getPublicKey();
      int decrypted = 1;
      while (d > 0 ) {
          decrypted *= encryptedText;
          decrypted %= n;
          d -= 1;
      }

      d = receiver.getPublicKey();
      while (d > 0 ) {
          decrypted *= encryptedText;
          decrypted %= n;
          d -= 1;
      }
      return decrypted;
    }

    public static List<Integer> encoder(String message, User sender, User receiver) {
        List<Integer> encoded = new ArrayList<>();
        for (char letter : message.toCharArray()) {
            encoded.add(encrypt((int)letter, sender, receiver));
        }
        return encoded;
    }

    public static String decoder(List<Integer> encoded, User sender, User receiver) {
        StringBuilder s = new StringBuilder();
        for (int num : encoded) {
            s.append((char)decrypt(num, sender, receiver));
        }
        return s.toString();
    }
}
