import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class Problem3 {
    public static HashSet<Integer> prime = new HashSet<>();
    public static Random random = new Random();
    public static Integer n = null;
    public static void main(String[] args) {
        sieveOfEratosthenes();
        for (Integer primeNum : prime) {
            System.out.println(primeNum);
        }
        System.out.println("size: " + prime.size());
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

    public static void

    public static void chooseKeys() {
        int prime1 = pickPrimeNumber();
        int prime2 = pickPrimeNumber();

        n = prime1 * prime2;
        int e = (prime1 - 1) * (prime2 -1);
        int
    }
}
