/**
 * Primality check function.
 *
 * @author Stephen J. Hartley
 * @version 2004 July
 */
public class Prime {

  /**
    * Determine primality (effective but not efficient).
    * @param k The number to check for primality.
    * @return True if prime else false.
    */
  public static boolean prime(int k) {
     if (k < 2) return false;
     int limit = k/2;
     for (int i = 2; i <= limit; i++) {
        if ((k % i) == 0) return false;
     }
     return true;
  } 
}
