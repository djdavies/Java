class HelloWorld {

    public static int execute (int n) {
        int sum = 0;
        for (int l= 0; l < n; ++l) {
            sum =+ 1;
        }
        return sum;
    }

    public static void main(String[] args) {
        int R = 10;
        for (int n = 100; n < 10000; n += 100) {
            long total = 0;
            for (int r = 0; r < R; ++r) {
                // Measure time
                long start_time = System.nanoTime ();
                // ... code being measured
                execute (n);
                total += System.nanoTime() - start_time; 
    }
    System.out.println(n + " , " +
    (double)total / (double)R);
}
}
}
