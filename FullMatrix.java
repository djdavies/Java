public class FullMatrix {
    public static final int NR = 5;
    public static final int NC = 6;

    public static void main(String [] args)
    {
        int [][] a = new int[NR][NC];
        int [][] b = new int[NR][NC];
        int [][] c;

        for (int i =0; i < NR; i++)
            for (int j = 0; j < NC; j++)
                a[i][j] = b[i][j] = 0;

        a[0][5] = 2; a[1][2] = 3; a[3][1] = 7; a[3][4] = 2;
        a[4][1] = 1;
        b[0][1] = 1; b[1][0] = 6; b[1][2] = 5; b[2][3] = 9;
        b[3][0] = 2; b[3][4] = 5; b[4][1] = 1;

        c = Add(a,b);

        System.out.println("Array a:"); Print(a);
        System.out.println("Array b:"); Print(b);
        System.out.println("Array c:"); Print(c);
    }

    public static int[][] Add(int[][] a, int [][] b)
    {
        int[][] c = new int[NR][NC];

        for (int i=0; i<NR; i++)
            for (int j=0; j<NC; j++)
                c[i][j] = a[i][j] + b[i][j];

        return c;
    }

    public static void Print(int [][] a)
    {
        for (int i=0; i<NR; i++) {
            for (int j=0;j<NC;j++)
                System.out.print(a[i][j]+" ");
            System.out.println();
        }
    }
}
