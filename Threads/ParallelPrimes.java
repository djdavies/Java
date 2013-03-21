/**
 * A pool of threads is created to check a range of consecutive numbers
 * for primality.  Each thread gets the next unchecked number, checks
 * the number for primality, stores the result, then gets the next
 * unchecked number, etc.
 *
 * @author Stephen J. Hartley
 * @version 2004 July
 * Modified by Andrew Jones 20 March 2013
 * Modified by Jake Davies 21 March 2013
 */

class ParallelPrimes implements Runnable {
  private final int n1;
  private final int n2;
  private final int nChecked;
  private final int nThreads;
  private int next;
  private boolean[] taken;
  private boolean[] isPrime;

  /**
    * Constructor.
    * @param n1 The lower number in the range.
    * @param n2 The larger number in the range.
    * @param nThreads The number of threads in the pool.
    * @throws IllegalArgumentException
    */
  public ParallelPrimes(int n1, int n2, int nThreads) {
     this.n1 = n1;   
     this.n2 = n2;   
     this.nThreads = nThreads;
     nChecked = n2 - n1 + 1;
// The number of integers to check

     if (nChecked < 1 || nThreads > nChecked)
        throw new IllegalArgumentException("nChecked < 1 || nThreads > nChecked");
     taken = new boolean[nChecked];
// taken[] is an array of booleans indicating whether any particular number has yet been
// "taken" for testing to see whether it's prime or not.
// taken[0] indicates whether n1 + 0 = n1 has been tested for primality;
// taken[i] indicates whether the number n1 + i has been tested for primality.

     isPrime = new boolean[nChecked];
// similarly, isPrime[0] will eventually hold a boolean indicating whether n1 is prime;
// isPrime[i] will indicate whether n1 + i is prime.
// e.g. if n1=100, isPrime[41] will indicate whether 141 has been found to be prime or not.

     for (int i = 0; i < nChecked; i++) taken[i] = isPrime[i] = false;
// Not prime unless we discover (later on) that it is ...

     next = 0;
// next -- next + n1 is the next number to check for primality.
  }

  /**
    * Code for threads inside this object to execute.
    */
  public void run() {
     int mine = 0;
     while (true) {
         synchronized (this) {
// loop forever

        // need to ensure only 1 thread at a time
        while (next < nChecked && taken[next]) next++;
// keep on moving through the taken[] array until we find a number (n1 + next)
// which hasn't been checked for prime-ness

        // the value of next could change here...
        mine = next;
// temporary variable "mine" now holds the subscript of next one to be checked
// (so if some other thread changes "next" after this point, it doesn't matter)

        if (mine >= nChecked) return;
// but return when all the numbers to be checked have been inspected

        // this needs to be synchronised
        taken[mine] = true;
// "Claim" this number for us to test -- in doing the exercise, bear in mind
// that we don't want any other thread to claim the same number!

// CODE OMITTED TO DO THE FOLLOWING:
// IF the result of calling the static method prime() in class Prime with parameter
// (n1 + mine) is true
// THEN set isPrime[mine] to true.
// (In other words, isPrime[mine] gets set to true to indicate that (n1 + mine) is prime)

// PUT THE MISSING CODE IN HERE!!
         } // end syncrhonized block here
if (Prime.prime(n1+mine)) isPrime[mine] = true;
     }
  }

  /**
    * Field accessor function.
    * @param i The number to query for primality.
    * @return True if prime otherwise false.
    */
  public boolean isPrime(int i) { return isPrime[i]; }

  /**
    * Driver.
    * @param args Command line arguments.
    */
  public static void main(String[] args) {
     int n1 = 0, n2 = 0, nThreads = 0;
     try {
        n1 = Integer.parseInt(args[0]);
        n2 = Integer.parseInt(args[1]);
        nThreads = Integer.parseInt(args[2]);
// 1st argument: the first number to check for prime-ness
// 2nd argument: the last number to check for prime-ness
// 3rd argument: number of separate threads working on the set of numbers to be tested

     } catch (NumberFormatException e) {
        System.out.println("improper format");
        System.exit(1);
     } catch (ArrayIndexOutOfBoundsException e) {
        System.out.println("not enough command line arguments");
        System.exit(1);
     }
     System.out.println("printing primes from " + n1 + " to "
        + n2 + " using " + nThreads + " threads");
     // All threads execute inside the SAME object and thus SHARE all data.
     ParallelPrimes pp = new ParallelPrimes(n1, n2, nThreads);
     Thread[] t = new Thread[nThreads];
     for (int i = 0; i < nThreads; i++) t[i] = new Thread(pp);

     for (int i = 0; i < nThreads; i++) t[i].start();
     try {
        for (int i = 0; i < nThreads; i++) t[i].join();
     } catch (InterruptedException e) { /* ignored */ }
// Run all the threads, then wait for them to complete

     for (int i = 0; i < n2 - n1 + 1; i++)
        if (pp.isPrime(i)) System.out.println((n1 + i)  + " is prime");
// Print out the primes found
  }
}
