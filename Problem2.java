public class Problem2 {
    private static double lossiness = 0.2;  // setting default lossy compression to 20%

    public static void main(String[] args) {
        // making test users
        User Alice = new User();
        User Bob = new User();
        
        Message message1 = new Message(Alice, Bob, null, "Hello, this is a test message with some fine details!");

        // printing original message
        System.out.println("Initial message:");
        System.out.println(message1.getMessageBody());

        // compressing and showing compressed version
        Message compressedMessage = compressMessage(message1);
        System.out.println("\nThe compressed message:");
        System.out.println(compressedMessage.getMessageBody());

        // decompressing and showing result
        Message decompressedMessage = decompressMessage(compressedMessage);
        System.out.println("\nDecompressed message:");
        System.out.println(decompressedMessage.getMessageBody());
    }

    public static Message compressMessage(Message originalMessage) {
        String original = originalMessage.getMessageBody();
        int originalLength = original.length();
        
        // changing the string into numbers we can use
        double[] samples = new double[originalLength];
        for (int i = 0; i < originalLength; i++) {
            samples[i] = (double) original.charAt(i);
        }
        
        // need to make the array a power of 2 for FFT
        int paddedLength = nextPowerOf2(originalLength);
        double[] paddedSamples = new double[paddedLength];
        System.arraycopy(samples, 0, paddedSamples, 0, originalLength);
        
        Complex[] fft = fft(paddedSamples);
        
        // doing the lossy compression by getting rid of high frequencies
        int cutoff = (int) (fft.length * (1.0 - lossiness));
        for (int i = cutoff; i < fft.length; i++) {
            fft[i] = new Complex(0, 0);
        }
        
        // making the compressed data into a string
        StringBuilder compressed = new StringBuilder();
        for (int i = 0; i < cutoff; i++) {
            compressed.append(String.format("%.2f,%.2f;", fft[i].real(), fft[i].imag()));
        }
        
        // adding metadata to keep track of stuff
        String metadata = "FFT:" + originalLength + ":" + lossiness;
        
        return new Message(
            originalMessage.getSender(),
            originalMessage.getReceiver(),
            metadata,
            compressed.toString()
        );
    }

    public static Message decompressMessage(Message compressedMessage) {
        // getting info from metadata
        String[] metadataParts = compressedMessage.getMetadata().split(":");
        int originalLength = Integer.parseInt(metadataParts[1]);
        
        // breaking down the compressed string
        String[] coefficients = compressedMessage.getMessageBody().split(";");
        int paddedLength = nextPowerOf2(originalLength);
        Complex[] fft = new Complex[paddedLength];
        
        // putting FFT array back together
        for (int i = 0; i < coefficients.length - 1; i++) {
            String[] parts = coefficients[i].split(",");
            fft[i] = new Complex(
                Double.parseDouble(parts[0]),
                Double.parseDouble(parts[1])
            );
        }
        // filling in the rest with zeros
        for (int i = coefficients.length - 1; i < paddedLength; i++) {
            fft[i] = new Complex(0, 0);
        }
        
        // doing inverse FFT to get back the message
        double[] samples = ifft(fft);
        
        // turning numbers back into text
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

    // class for handling complex numbers needed for FFT
    private static class Complex {
        private final double real;
        private final double imag;
        
        public Complex(double real, double imag) {
            this.real = real;
            this.imag = imag;
        }
        
        public double real() { return real; }
        public double imag() { return imag; }
        
        // math operations for complex numbers
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
    }

    // converts regular numbers to complex for FFT
    private static Complex[] fft(double[] x) {
        int n = x.length;
        Complex[] result = new Complex[n];
        
        for (int i = 0; i < n; i++) {
            result[i] = new Complex(x[i], 0);
        }
        
        return fft(result);
    }

    // actual FFT implementation
    private static Complex[] fft(Complex[] x) {
        int n = x.length;
        
        if (n == 1) {
            return x;
        }
        
        // splitting into even and odd parts
        Complex[] even = new Complex[n/2];
        Complex[] odd = new Complex[n/2];
        for (int i = 0; i < n/2; i++) {
            even[i] = x[2*i];
            odd[i] = x[2*i + 1];
        }
        
        // recursive FFT calls
        Complex[] evenFFT = fft(even);
        Complex[] oddFFT = fft(odd);
        
        // combining results
        Complex[] result = new Complex[n];
        for (int k = 0; k < n/2; k++) {
            double kth = -2 * k * Math.PI / n;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            result[k] = evenFFT[k].plus(wk.times(oddFFT[k]));
            result[k + n/2] = evenFFT[k].minus(wk.times(oddFFT[k]));
        }
        
        return result;
    }

    // inverse FFT to get back original signal
    private static double[] ifft(Complex[] x) {
        int n = x.length;
        
        // need to take complex conjugate first
        Complex[] conjugate = new Complex[n];
        for (int i = 0; i < n; i++) {
            conjugate[i] = new Complex(x[i].real(), -x[i].imag());
        }
        
        Complex[] result = fft(conjugate);
        
        // getting real numbers back
        double[] realResult = new double[n];
        for (int i = 0; i < n; i++) {
            realResult[i] = result[i].real() / n;
        }
        
        return realResult;
    }

    // helper to get next power of 2 for FFT
    private static int nextPowerOf2(int n) {
        int power = 1;
        while (power < n) {
            power *= 2;
        }
        return power;
    }
    
    // lets us change how lossy the compression is
    public static void setLossiness(double newLossiness) {
        if (newLossiness < 0 || newLossiness > 1) {
            throw new IllegalArgumentException("Lossiness must be between 0 and 1");
        }
        lossiness = newLossiness;
    }
}