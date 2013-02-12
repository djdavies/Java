import java.util.*;

class RandomArray {
	
	int[] array;
	int size;
	
    public RandomArray (int n) {
        array = new int[n];
		for (int i = 0; i < n; i++) {
			array[i] = (int)(Math.random() * (double) Integer.MAX_VALUE);
		}
		size = n;
    }

	public void sortArray () {
		Arrays.sort(array);
	}

	public int search (int key) {	
		return Arrays.binarySearch(array, key); // search for 1		
	}

    public static void main (String args[]) {
        int R = 10;
        for (int n = 100; n < 100000; n += 100) {
            long total = 0;
            for (int r = 0; r < R; ++r) {
				RandomArray A = new RandomArray (n);
				A.sortArray ();
                long start = System.nanoTime ();
				A.search(-1);
                total += System.nanoTime () - start;
            }
            System.out.println (n + "," +
            (double)total / (double)R);
        }
    }
}
