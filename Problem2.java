public class Problem2 {
    private static final int SAMPLING_RATE = 44100;  // Standard audio sampling rate
    private static double lossiness = 0.2;  // Default 20% lossy compression

    public static void main(String[] args) {
        // Create test users
        User Alice = new User();
        User Bob = new User();
        
        // Create a test message
        Message message1 = new Message(Alice, Bob, null, "Hello, this is a test message with some fine details that might get blurry!");

        // Demonstrate compression and decompression
        System.out.println("Original message:");
        System.out.println(message1.getMessageBody());

        // Compress the message
        Message compressedMessage = compressMessage(message1);
        System.out.println("\nCompressed message (length: " + compressedMessage.getMessageBody().length() + "):");
        System.out.println(compressedMessage.getMessageBody());

        // Decompress the message
        Message decompressedMessage = decompressMessage(compressedMessage);
        System.out.println("\nDecompressed message:");
        System.out.println(decompressedMessage.getMessageBody());
    }

    // FFT compression
    public static Message compressMessage(Message originalMessage) {
        String original = originalMessage.getMessageBody();
        int originalLength = original.length();
        
        // Convert string to numerical samples
        double[] samples = new double[originalLength];
        for (int i = 0; i < originalLength; i++) {
            samples[i] = (double) original.charAt(i);
        }
        
        // Pad the array to the next power of 2 if necessary
        int paddedLength = nextPowerOf2(originalLength);
        double[] paddedSamples = new double[paddedLength];
        System.arraycopy(samples, 0, paddedSamples, 0, originalLength);
        
        // Apply FFT
        Complex[] fft = fft(paddedSamples);
        
        // Apply lossy compression by zeroing out high frequencies
        int cutoff = (int) (fft.length * (1.0 - lossiness));
        for (int i = cutoff; i < fft.length; i++) {
            fft[i] = new Complex(0, 0);
        }
        
        // Convert FFT coefficients to string representation
        StringBuilder compressed = new StringBuilder();
        for (int i = 0; i < cutoff; i++) {
            compressed.append(String.format("%.2f,%.2f;", fft[i].real(), fft[i].imag()));
        }
        
        return new Message(
            originalMessage.getSender(),
            originalMessage.getReceiver(),
            "FFT_COMPRESSED:" + originalLength + ":" + lossiness,
            compressed.toString()
        );
    }

    // FFT decompression
    public static Message decompressMessage(Message compressedMessage) {
        String[] metadata = compressedMessage.getMetadata().split(":");
        int originalLength = Integer.parseInt(metadata[1]);
        
        // Parse the compressed string back to Complex numbers
        String[] coefficients = compressedMessage.getMessageBody().split(";");
        int paddedLength = nextPowerOf2(originalLength);
        Complex[] fft = new Complex[paddedLength];
        
        // Reconstruct FFT array
        for (int i = 0; i < coefficients.length - 1; i++) {
            String[] parts = coefficients[i].split(",");
            fft[i] = new Complex(
                Double.parseDouble(parts[0]),
                Double.parseDouble(parts[1])
            );
        }
        for (int i = coefficients.length - 1; i < paddedLength; i++) {
            fft[i] = new Complex(0, 0);
        }
        
        // Apply inverse FFT
        double[] samples = ifft(fft);
        
        // Convert back to string
        StringBuilder decompressed = new StringBuilder();
        for (int i = 0; i < originalLength; i++) {
            decompressed.append((char) Math.round(samples[i]));
        }
        
        return new Message(
            compressedMessage.getSender(),
            compressedMessage.getReceiver(),
            "PLAIN",
            decompressed.toString()
        );
    }

    // Helper class for complex numbers
    private static class Complex {
        private final double real;
        private final double imag;
        
        public Complex(double real, double imag) {
            this.real = real;
            this.imag = imag;
        }
        
        public double real() { return real; }
        public double imag() { return imag; }
        
        public Complex plus(Complex other) {
            return new Complex(real + other.real, imag + other.imag);
        }
        
        public Complex minus(Complex other) {
            return new Complex(real - other.real, imag - other.imag);
        }
        
        public Complex times(Complex other) {
            return new Complex(
                real * other.real - imag * other.imag,
                real * other.imag + imag * other.real
            );
        }
        
        public Complex scale(double factor) {
            return new Complex(real * factor, imag * factor);
        }
    }

    // FFT implementation
    private static Complex[] fft(double[] x) {
        int n = x.length;
        Complex[] result = new Complex[n];
        
        // Convert real numbers to complex
        for (int i = 0; i < n; i++) {
            result[i] = new Complex(x[i], 0);
        }
        
        return fft(result);
    }

    private static Complex[] fft(Complex[] x) {
        int n = x.length;
        
        if (n == 1) {
            return x;
        }
        
        // Split into even and odd
        Complex[] even = new Complex[n/2];
        Complex[] odd = new Complex[n/2];
        for (int i = 0; i < n/2; i++) {
            even[i] = x[2*i];
            odd[i] = x[2*i + 1];
        }
        
        // Recursive FFT on even and odd parts
        Complex[] evenFFT = fft(even);
        Complex[] oddFFT = fft(odd);
        
        // Combine results
        Complex[] result = new Complex[n];
        for (int k = 0; k < n/2; k++) {
            double kth = -2 * k * Math.PI / n;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            result[k] = evenFFT[k].plus(wk.times(oddFFT[k]));
            result[k + n/2] = evenFFT[k].minus(wk.times(oddFFT[k]));
        }
        
        return result;
    }

    // Inverse FFT
    private static double[] ifft(Complex[] x) {
        int n = x.length;
        
        // Take complex conjugate
        Complex[] conjugate = new Complex[n];
        for (int i = 0; i < n; i++) {
            conjugate[i] = new Complex(x[i].real(), -x[i].imag());
        }
        
        // Apply FFT
        Complex[] result = fft(conjugate);
        
        // Take complex conjugate and scale
        double[] realResult = new double[n];
        for (int i = 0; i < n; i++) {
            realResult[i] = result[i].real() / n;
        }
        
        return realResult;
    }

    // Utility method to find next power of 2
    private static int nextPowerOf2(int n) {
        int power = 1;
        while (power < n) {
            power *= 2;
        }
        return power;
    }
    
    // Setter for lossiness
    public static void setLossiness(double newLossiness) {
        if (newLossiness < 0 || newLossiness > 1) {
            throw new IllegalArgumentException("Lossiness must be between 0 and 1");
        }
        lossiness = newLossiness;
    }
}