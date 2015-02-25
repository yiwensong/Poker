package rules;
import deck.*;
import java.util.Random;


/** Rules is a class where we define all the poker hands and rank them.
 *  Hand will be another class in this package that implements Comparable
 */

/** TODO.
 *  When we do AI, we should extend this class to find draws.
 */

public class Rules {
  
  /** The following methods should return whether or not a 5 card hand
   *  has the property stated by the name of the function.
   *  Ex: 'sample' is a hand consistening of [As Ks Qs Js Ts] -
   *    royalFlush(sample) returns true
   *    straightFlush(sample) returns true
   *    quads(sample) returns false
   *    fullHouse(sample) returns false
   *    flush(sample) returns true
   *    etc.
   */
  
  public static boolean royalFlush(short[] hand) {
    short[] hands = handToVal(hand);
    return straightFlush(hand) && (hands[0] == 8);
  }

  public static boolean straightFlush(short[] hand) {
    return straight(hand) && flush(hand);
  }

  public static boolean quads(short[] hand) {
    return maxRep(hand) >= 4;
  }

  public static boolean fullHouse(short[] hand) {
    hand = handToVal(hand);
    return ((hand[0]==hand[1]) && (hand[1]==hand[2]) && (hand[3]==hand[4]))
      || ((hand[0] == hand[1]) && (hand[2]==hand[3]) && (hand[3]==hand[4]));
  }

  public static boolean flush(short[] hand) {
    hand = sortSuit(hand);
    return (hand[0]==hand[1] && hand[1]==hand[2] && hand[2]==hand[3] && hand[3]==hand[4]);
  }

  public static boolean straight(short[] hand) {
    short[] hands = sort(hand);
    boolean lowA = true;
    for(int i=0;i<5;i++) {
      hands[i] = (short)(hands[i] / 4);
    }
    for(int i=1;i<5;i++) {
      if((hands[i-1] + 1) != hands[i]){
        lowA = false;
        break;
      }
    }

    hands = handToVal(hand);
    boolean highA = true;
    for(int i=1;i<5;i++) {
      if((hands[i-1] + 1) != hands[i]){
        highA = false;
        break;
      }
    }

    return (lowA || highA);
  }

  public static boolean trips(short[] hand) {
    return maxRep(hand) >= 3;
  }

  public static boolean twoPair(short[] hand) {
    hand = handToVal(hand);
    boolean a,b,c,d;
    a = hand[0] == hand[1];
    b = hand[1] == hand[2];
    c = hand[2] == hand[3];
    d = hand[3] == hand[4];
    return (a || b) && (c || d);
  }

  public static boolean pair(short[] hand) {
    return maxRep(hand) >= 2;
  }

  /* Classifies hand:
   * 0: High card
   * 1: Pair
   * 2: 2 Pair
   * 3: Trips
   * 4: Straight
   * 5: Flush
   * 6: Full House
   * 7: Quads
   * 8: Straight Flush
   * 9: Royal
   */
  public static short classifyHand(short[] hand) {
    if(royalFlush(hand)) {
      return 9;
    }
    if(straightFlush(hand)) {
      return 8;
    }
    if(quads(hand)) {
      return 7;
    }
    if(fullHouse(hand)) {
      return 6;
    }
    if(flush(hand)) {
      return 5;
    }
    if(straight(hand)) {
      return 4;
    }
    if(trips(hand)) {
      return 3;
    }
    if(twoPair(hand)) {
      return 2;
    }
    if(pair(hand)) {
      return 1;
    }
    return 0;
  }

  /** The following methods are a way to determine a winner in
   *  the case of a hand-type tie.
   *  The return values are as follows: 
   *    0 - 2
   *    1 - 3
   *    2 - 4
   *    3 - 5
   *    4 - 6
   *    5 - 7
   *    6 - 8
   *    7 - 9
   *    8 - T
   *    9 - J
   *    10 - Q
   *    11 - K
   *    12 - A
   *
   *  NOTE: Straight(flush) to 5 also needs a special method at
   *  some point.
   */
  /* Returns value of highest card in hand */
  public static short highCard(short[] hand) {
    if(lowAce(hand)) {
      hand = sort(hand);
    } else {
      hand = sortHighA(hand);
    }
    return hand[4];
  }

  /* Returns value of highest pair in hand */
  public static short highPair(short[] hand) {
    hand = sortHighA(hand);
    for(int i=4;i>0;i--){
      if(cardRank(hand[i])==cardRank(hand[i-1])){
        return cardRank(hand[i]);
      }
    }
    return -1;
  }

  /* Returns value of lowest pair in hand */
  public static short lowPair(short[] hand) {
    hand = sortHighA(hand);
    for(int i=1;i<5;i++){
      if(cardRank(hand[i])==cardRank(hand[i-1])){
        return cardRank(hand[i]);
      }
    }
    return -1;
  }

  /* Returns value of triple */
  public static short highTrip(short[] hand) {
    hand = sortHighA(hand);
    for(int i=2;i<5;i++){
      if(cardRank(hand[i])==cardRank(hand[i-1]) && cardRank(hand[i])==cardRank(hand[i-2])){
        return cardRank(hand[i]);
      }
    }
    return -1;
  }

  /* Returns value of quad */
  public static short highQuad(short[] hand) {
    hand = sortHighA(hand);
    for(int i=3;i<5;i++){
      if(cardRank(hand[i])==cardRank(hand[i-1]) && cardRank(hand[i])==cardRank(hand[i-2])
          && cardRank(hand[i])==cardRank(hand[i-3])){
        return cardRank(hand[i]);
      }
    }
    return -1;
  }

  /* Returns true if Ace is low */
  public static boolean lowAce(short[] hand) {
    hand = sort(hand);
    for(short i=0;i<5;i++) {
      if(hand[i]/4 != i) {
        return false;
      }
    }
    return true;
  }

  /** Utility functions.
   *  For ease of use.
   */

  /* Converts a hand into card values, sorted */
  public static short[] handToVal(short[] hand) {
    hand = sort(hand);
    for(int i=0;i<5;i++) {
      hand[i] = cardRank(hand[i]);
    }
    return hand;
  }

  /* Converts a hand into card values, unsorted */
  public static short[] handToValU(short[] hand) {
    for(int i=0;i<5;i++) {
      hand[i] = cardRank(hand[i]);
    }
    return hand;
  }
    
  /* Finds the number of maximum repeat cards in hand */
  public static short maxRep(short[] hand) {
    short mr = 1;
    short tmp = 1;
    short lst = -1;
    hand = handToVal(hand);
    for(int i=1;i<5;i++) {
      if(hand[i]==hand[i-1]) {
        tmp++;
        if(tmp > mr) {
          mr ++;
        }
      } else {
        tmp = 1;
      }
    }
    return mr;
  }

  /* Sorts hand by card number */
  public static short[] sort(short[] hand) {
    /* Assume hand size is 5 */
    short[] hd = new short[5];
    for(int i=0;i<5;i++){
      hd[i] = hand[i];
    }
    hand = hd;
    for(int i=5;i>0;i--){
      for(int j=1;j<i;j++){
        if(hand[j]<hand[j-1]){
          short tmp = hand[j-1];
          hand[j-1] = hand[j];
          hand[j] = tmp;
        }
      }
    }
    return hand;
  }

  /* Sorts hand by card number, with highest number in front */
  public static short[] sortRev(short[] hand) {
    /* Assume hand size is 5 */
    for(int i=5;i>0;i--){
      for(int j=1;j<i;j++){
        if(hand[j]<hand[j-1]){
          short tmp = hand[j-1];
          hand[j-1] = hand[j];
          hand[j] = tmp;
        }
      }
    }
    return hand;
  }

  /* Sorts hand with high Ace */
  public static short[] sortHighA(short[] hand) {
    /* Assume hand size is 5 */
    for(int i=5;i>0;i--){
      for(int j=1;j<i;j++){
        if( cardRank(hand[j])<cardRank(hand[j-1]) || 
            ((cardRank(hand[j])==cardRank(hand[j-1]) && hand[j]<hand[j-1])) ){
          short tmp = hand[j-1];
          hand[j-1] = hand[j];
          hand[j] = tmp;
        }
      }
    }
    return hand;
  }

  /* Sorts hand with high Ace, with Highest in front */
  public static short[] sortHighARev(short[] hand) {
    /* Assume hand size is 5 */
    for(int i=5;i>0;i--){
      for(int j=1;j<i;j++){
        if( cardRank(hand[j])>cardRank(hand[j-1]) || 
            ((cardRank(hand[j])==cardRank(hand[j-1]) && hand[j]>hand[j-1])) ){
          short tmp = hand[j-1];
          hand[j-1] = hand[j];
          hand[j] = tmp;
        }
      }
    }
    return hand;
  }

  /* Sorts by suit */
  public static short[] sortSuit(short[] hand) {
    /* Assume hand size is 5 */
    for(int i=5;i>0;i--){
      for(int j=1;j<i;j++){
        if(suitNum(hand[j])<suitNum(hand[j-1]) || 
            ((suitNum(hand[j])==suitNum(hand[j-1])) && cardRank(hand[j])<cardRank(hand[j-1]))){
          short tmp = hand[j-1];
          hand[j-1] = hand[j];
          hand[j] = tmp;
        }
      }
    }
    return hand;
  }

  /* Sorts by suit with low Ace */
  public static short[] sortSuitLowAce(short[] hand) {
    /* Assume hand size is 5 */
    for(int i=5;i>0;i--){
      for(int j=1;j<i;j++){
        if(suitNum(hand[j])<suitNum(hand[j-1]) || 
            ((suitNum(hand[j])==suitNum(hand[j-1])) && hand[j]<hand[j-1])){
          short tmp = hand[j-1];
          hand[j-1] = hand[j];
          hand[j] = tmp;
        }
      }
    }
    return hand;
  }

  public static short cardRank(short card){
    return (card/4-1 < 0) ? (short)(card/4+12) : (short)(card/4-1);
  }

  public static short suitNum(short card){
    return (short)(card%4);
  }

  public static String handToString(short[] hand) {
    if(lowAce(hand)){
      hand = sort(hand);
    } else {
      hand = sortHighA(hand);
    }
    String s = "";
    for(int i=0;i<5;i++){
      s += deck.Deck.num2str(hand[i]);
      s += " ";
    }
    return s;
  }

  /* Test cases */
  public static void main(String[] args) {
    // Write some tests

    /* Test cases for sorting */
    short[] sampleHand1 = {23,6,2,7,22};
    sampleHand1 = sort(sampleHand1);
    System.out.println(handToString(sampleHand1));
    System.out.println(twoPair(sampleHand1));

    short[] sampleHand2 = {31,42,50,12,0};
    sampleHand2 = sortHighA(sampleHand2);
    System.out.println(handToString(sampleHand2));
    System.out.println(maxRep(sampleHand2));

    short[] sampleHand3 = {0,1,2,3,4};
    sampleHand3 = sort(sampleHand3);
    System.out.println(handToString(sampleHand3));
    System.out.println(maxRep(sampleHand3));

    Deck deck = new Deck();
    short[] sampleHand4 = new short[5];
    for(int i=0;i<5;i++) {
      sampleHand4[i] = deck.deal();
    }
    sampleHand4 = sortHighA(sampleHand4);
    System.out.println(handToString(sampleHand4));
    System.out.println(maxRep(sampleHand4));
    
    deck = new Deck();
    short[] sampleHand5 = new short[5];
    for(int i=0;i<5;i++) {
      sampleHand5[i] = deck.deal();
    }
    sampleHand5 = sortSuit(sampleHand5);
    System.out.println(handToString(sampleHand5));
    System.out.println(highCard(sampleHand5));

    short[] sampleHand6 = {0,4,8,12,16};
    sampleHand6 = sortSuit(sampleHand6);
    System.out.println(handToString(sampleHand6));
    System.out.println(highCard(sampleHand6));

    short[] sampleHand7 = {20,4,8,12,16};
    sampleHand7 = sortSuit(sampleHand7);
    System.out.println(handToString(sampleHand7));
    System.out.println(highCard(sampleHand7));

    short[] sampleHand8 = {3,51,47,43,39};
    sampleHand8 = sortSuit(sampleHand8);
    System.out.println(handToString(sampleHand8));
    System.out.println(highCard(sampleHand8));
  }
}
