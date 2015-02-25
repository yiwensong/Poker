package deck;
import java.util.Random;

/** Deck is the class of all decks.
 *  Fields:
 *    short[] cards (private)
 *  Public Methods:
 *    Deck() - constructs a new shuffled deck
 *    Deck(long) - constructs a new seeded deck
 *    String toString() - prints out the order of the Deck in a nice order
 *    String num2str(short) - gives the name of card that a number represents
 *    void shuffle() - shuffles the deck psuedo-randomly
 *    void main() - tests
 *  Private Methods:
 *    void shuffle(long) - seeds the deck
 *    long str2num(String) - stupid workaround
 */

public class Deck {

  /** Fields
   *  short[] cards - the order of the deck
   */
  private short[] cards;
  private short pos;

  /** Constructors
   *  Deck() - constructs a shuffled deck
   *  Deck(seed) - constructs a seeded deck
   */
  public Deck() {
    cards = new short[52];
    for(short i=0;i<52;i++){
      cards[i] = i;
    }
    shuffle();
  }

  public Deck(long seed) {
    cards = new short[52];
    for(short i=0;i<52;i++){
      cards[i] = i;
    }
    shuffle(seed);
  }

  /** Private methods
   *  shuffle() - shuffles the deck ***CHANGED: This is now a public method***
   *  shuffle(long) - seeds the deck
   */
  public void shuffle() {
    pos = -1;
    short st = 0;
    short ofs, tmp;
    Random rnjesus = new Random();

    while (st<cards.length) {
      // Randomization code here
      ofs = (short)rnjesus.nextInt(cards.length - st);
      tmp = cards[st];
      cards[st] = cards[st + ofs];
      cards[st + ofs] = tmp;
      st++;
    }
  }

  private void shuffle(long seed) {
    pos = -1;
    short st = 0;
    short ofs, tmp;
    Random rnjesus = new Random(seed);

    while (st<cards.length) {
      // Randomization code here
      ofs = (short)rnjesus.nextInt(cards.length - st);
      tmp = cards[st];
      cards[st] = cards[st + ofs];
      cards[st + ofs] = tmp;
      st++;
    }
  }

  /** Public methods
   *  toString() - formats the deck.
   *  The deck is conveniently formatted into 9 columns, so, assuming a 9 player
   *  game, each player's hand are the first two cards in a column. The first column
   *  is the Small Blind, the second the Big Blind, the third UTG, etc until the last
   *  column which is the button.
   */
  public String toString() {
    String s = "";
    for(short i=0;i<cards.length;i++) {
      s += num2str(cards[i]);

      if(i%9==8) {
        s += '\n';
      } else {
        s += ' ';
      }

    }
    
    return s;
  }

  public short deal() {
    pos++;
    return cards[pos];
  }

  /** num2str(short) is a function that takes a card number and gives you its value.
   *  When called, it should take in a value between 0 and 51. The result should be
   *  given in the form: Vs - where V is value and s is  suit (eg. King of Spades 
   *  would be Ks). 
   *  The card value is determined by the num/4. If this value is 0, the card is an
   *  Ace, if 1, a 2, and so on, until 12, which is a King. A value higher than 12
   *  still gives a value of King, but we should do our best to avoid such scenarios,
   *  and we can make this method throw exceptions later.
   *  The suit is determined by num%4. The suits go up alphabetically (Clubs, Diamonds,
   *  Hearts, Spades) from 0 to 3. A value of more than 3 still gives Spades, but this
   *  is theoretically impossible.
   */
  public static String num2str(short num) {

    /* d is the value number, and r is the suit number */
    int d,r;
    d = num/4;
    r = num%4;

    /* We will return s at the end */
    String s = "";

    switch(d){
      case 0: s += 'A';
              break;
      case 1: s += '2';
              break;
      case 2: s += '3';
              break;
      case 3: s += '4';
              break;
      case 4: s += '5';
              break;
      case 5: s += '6';
              break;
      case 6: s += '7';
              break;
      case 7: s += '8';
              break;
      case 8: s += '9';
              break;
      case 9: s += 'T';
              break;
      case 10: s += 'J';
              break;
      case 11: s += 'Q';
              break;
      default: s += 'K';
    }

    switch(r){
      case 0: s += 'c';
              break;
      case 1: s += 'd';
              break;
      case 2: s += 'h';
              break;
      default: s += 's';
    }
    return s;
  }

  /* This method will leave once we figure out how Java Strings work
  private static long str2num(String str){
    char[] arr = str.toCharArray();
    long num = 0;
    for(int i=0;i<arr.length;i++){
      num *= 10;
      num += (long)arr[i] - (long)('0');
    }
    return num;
  }
  */

  /* Generate a shuffled deck or seeded deck
   * Generates decks until SB gets pocket Aces
   */
  public static void main(String[] args) {
    Deck test;
    if(args.length > 0){
      long c = Integer.parseInt(args[0]);
      test = new Deck(c);
    } else {
      test = new Deck();
    }
    System.out.println(test);
    System.out.println("");

    boolean pocketAforSB = false;
    int i;
    for(i=0;!pocketAforSB;i++){
      test = new Deck();
      pocketAforSB = (test.cards[0]/4==0)&&(test.cards[9]/4==0);
    }
    System.out.println(test);
    System.out.println("Hands until Small Blind got pocket Aces: " + i);

    test.shuffle();
    System.out.println(num2str(test.deal()) + " " + num2str(test.deal()) + " " + num2str(test.deal()) + " " + num2str(test.deal()) + " " + num2str(test.deal()) + " " + num2str(test.deal()) + " " + num2str(test.deal()) + " " + num2str(test.deal()) + " " + num2str(test.deal()) + " " + num2str(test.deal()) + " " + num2str(test.deal())); 
  }
}
