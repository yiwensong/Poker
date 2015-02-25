package test;
import java.util.Random;

/** The Test class is just for testing things.
 */

public class Test {

  private static final int SAMPLE_SIZE = 10000000;
  
  public static void main(String[] args) {
    Random rnjesus = new Random();

    double n = 0;

    for(int i=0;i<SAMPLE_SIZE;i++) {
      n += rnjesus.nextGaussian();
    }

    n /= SAMPLE_SIZE;

    System.out.println(n);
  }
}
