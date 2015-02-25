package rules;
import deck.*;

/** Hand will implement Comparable and will be a class to compare hands.
 *  It will utilize the Rules class to do help compare hands.
 */

public class Hand implements Comparable {

  /** Fields.
   */

  /* cards is the array of all useable cards a player has */
  short[] cards;

  /* worst are the indices of two excluded cards in hand (i.e. worst cards) */
  short[] worst;

  /* hClass (hand class) is a short[] that classifies the hand */
  short hClass;

  /* comps is an array of the card values, from high to low */
  short[] comps;

  /** Constructors.
   *  Null makes null.
   *  2 short[] makes table + hand
   *  1 short[] makes total hand
   *  Hand copies Hand
   */
  public Hand() {
    return;
  }

  public Hand(short[] hand) {
    cards = hand;
    hClass = classifyHand();
  }

  public Hand(short[] table, short[] hand){
    short[] compHand = new short[7];
    compHand[0] = table[0];
    compHand[1] = table[1];
    for(int i=0;i<5;i++) {
      compHand[i+2] = hand[i];
    }
    cards = compHand;
    hClass = classifyHand();
  }

  public Hand(Hand hand) {
    this.cards = hand.cards;
    this.worst = hand.worst;
  }

  /** Public methods.
   *  We implement compareTo(Object o) from the Comparable abstract class.
   */
  public int compareTo(Object h2) throws ClassCastException {
    hClass = classifyHand();
    ((Hand)h2).hClass = ((Hand)h2).classifyHand();

    if(hClass > ((Hand)h2).hClass){
      return 1;
    } else if (hClass < ((Hand)h2).hClass) {
      return -1;
    } else {
      for(int i=0;i<5;i++) {
        if(comps[i] < ((Hand)h2).comps[i]) {
          return -1;
        } else if (comps[i] > ((Hand)h2).comps[i]) {
          return 1;
        }
      }
    }
    return 0;
  }
  
  /** Private methods.
   *  Some helper functions and hand classification things
   */

  /* Returns an short that is the hand's class */
  private short classifyHand() {
    hClass = 0;
    short tmp;
    short[] hand = new short[5];
    comps = new short[5];
    for(int i=0;i<5;i++){
      comps[i] = -1;
    }
    for(short i=0;i<6;i++) {
      for(short j=(short)(i+1);j<7;j++) {

        // Copy all bits that are valid
        short dst = 0;
        short src = 0;
        while(dst<5) {
          if((src != i) && (src != j)) {
            hand[dst] = cards[src];
            dst++;
          }
          src++;
        }

        // Find the best hand in here
        // NEEDS WORK
        tmp = Rules.classifyHand(hand);
        if(tmp > hClass) {
          hClass = tmp;
          worst = new short[2];
          worst[0] = i;
          worst[1] = j;
          comps = makeComps5(hand,tmp);
        } else if(tmp == hClass) {
          // System.out.println("tmp == hClass (" + tmp + ")");

          short[] newComp = makeComps5(hand,tmp);
          for(short q=0;q<5;q++){
            if(newComp[q]>comps[q]){
              // swap
              worst = new short[2];
              worst[0] = i;
              worst[1] = j;
              comps = newComp;
              break;
            } else if (newComp[q]<comps[q]) {
              // Do nothing
              break;
            } else {
              // Inconclusive
              continue;
            }
          }

        }

      }
    }
    return hClass;
  }

  /* Returns a short array that is the list of numbers to compare. Low Ace is 1, 2 is 2, ...,
   * high Ace is 14.
   * Compares 0 index number first. For quads, we will put this as QQQQX, were Q is the value
   *  of the quad, and X is the kicker.
   * For full house, we will sort by TTTPP, where T is the triple and P is the pair.
   * For 2 pair, we will sort by HHLLX, where H is the high pair, L is the low pair, and X is
   *  the kicker
   * For pair, we will sort PPXXX.
   * For Straight, Straight Flush, and Royal Flush, we will sort L->H.
   * For Flush, we will sort H->L.
   *
   * CURRENTLY UNUSED AND UNIMPLEMENTED
   */
  private short[] getCompArray() {

    /** NOT IMPLEMENTED.
     */

    short[] cmp = new short[5];
    return cmp;
  }

  private static short[] makeComps5(short[] partial,short hc) {
    // partial is a hand of 5 (cards in numbers), hc is the code for the hand type
    switch(hc) {
      case 0: // High card
        partial = Rules.handToValU(Rules.sortHighARev(partial));
        break;
      case 1: // Pair
        partial = pairSort(partial);
        break;
      case 2: // 2 Pair
        partial = twoPairSort(partial);
        break;
      case 3: // Trips
        partial = tripSort(partial);
        break;
      case 4: // Straight
        partial = ascendSort(partial);
        break;
      case 5: // Flush
        partial = Rules.handToValU(Rules.sortHighARev(partial));
        break;
      case 6: // Full house
        partial = tripSort(partial);
        break;
      case 7: // Quads
        partial = quadSort(partial);
        break;
      default: // Straight Flush or Royal Flush
        partial = ascendSort(partial);
    }
    return partial;
  }

  /* Sorting techniques */

  /* for sorting pairs */
  private static short[] pairSort(short[] partial) {
    short idx = 2;
    short pidx = 0;
    short p = Rules.highPair(partial);
    short[] tmp = new short[5];

    partial = Rules.sortHighARev(partial);
    
    for(int i=0;i<5;i++) {
      if(Rules.cardRank(partial[i]) == p) {
        tmp[pidx] = partial[i];
        pidx++;
      } else {
        tmp[idx] = partial[i];
        idx++;
      }
    }
    partial = Rules.handToValU(tmp);
    return partial;
  }

  /* for sorting 2 pairs */
  private static short[] twoPairSort(short[] partial) {
    short ph = Rules.highPair(partial);
    short pl = Rules.lowPair(partial);
    short[] tmp = new short[5];
    for(int i=0;i<5;i++) {
      if( (Rules.cardRank(partial[i]) != ph) && (Rules.cardRank(partial[i]) != pl) ) {
        tmp[4] = Rules.cardRank(partial[i]);
      }
    }
    tmp[0] = ph;
    tmp[1] = ph;
    tmp[2] = pl;
    tmp[3] = pl;
    return tmp;
  }

  /* for sorting trips and full houses */
  private static short[] tripSort(short[] partial) {
    partial = Rules.sortHighARev(partial);

    short idx = 3;
    short tidx = 0;
    short t = Rules.highTrip(partial);
    short[] tmp = new short[5];
    for(int i=0;i<5;i++) {
      if( Rules.cardRank(partial[i]) == t)  {
        tmp[tidx] = partial[i];
        tidx++;
      } else {
        tmp[idx] = partial[i];
        idx++;
      }
    }
    partial = Rules.handToValU(tmp);
    return partial;
  }

  /* for sorting all types of straights */
  private static short[] ascendSort(short[] partial) { 
    if(Rules.lowAce(partial)) {
      partial = Rules.sort(partial);
    } else {
      partial = Rules.sortHighA(partial);
    }
    return partial;
  }

  /* for sorting quads */
  private static short[] quadSort(short[] partial) {
    short q = Rules.highQuad(partial);
    short[] tmp = new short[5];
    for(int i=0;i<5;i++) {
      if( (Rules.cardRank(partial[i]) != q) ) {
        tmp[4] = Rules.cardRank(partial[i]);
      }
    }
    tmp[0] = q; tmp[1] = q;
    tmp[2] = q; tmp[3] = q;
    return tmp;
  }

  public String toString() {
    String s = "";
    int sum = 0;
    for(int i=0;i<7;i++) {
      if( i!=worst[0] && i!=worst[1] ){
        s += Deck.num2str( cards[i] );
        sum++;
        if(sum<5) {
          s += ",";
        }
      }
    }
    return s;
  }

  /* MAIN */
  public static void main(String[] args) {
    // Tests

    Deck d = new Deck();

    short[] test1 = {0,1,2,3,4,5,6};
    Hand testHand1 = new Hand(test1);
    System.out.println("Hand class (hClass): " + testHand1.hClass);
    System.out.println("Comp array (comps): " + testHand1.comps[0] + ", "+ testHand1.comps[1] 
        + ", " + testHand1.comps[2] + ", "+ testHand1.comps[3] + ", "
        + testHand1.comps[4] + ", ");

    short[] test2 = {0,1,2,3,4,5,6};
    test2 = makeComps5(test2,(short)7);
    System.out.println("Comp array (comps): " + test2[0] + ", "+ test2[1] 
        + ", " + test2[2] + ", "+ test2[3] + ", "
        + test2[4] + ", ");

    short[] test3 = {0,1,2,3,4};
    test3 = quadSort(test3);
    System.out.println("Quad Sorted (quadSort): " + test3[0] + "," +test3[1] + "," +
        test3[2] + "," +test3[3] + "," +test3[4]);

    short[] test4 = {d.deal(),d.deal(),d.deal(),d.deal(),d.deal(),d.deal(),d.deal()};
    short[] test5 = {d.deal(),d.deal(),d.deal(),d.deal(),d.deal(),d.deal(),d.deal()};
    short[] test6 = {d.deal(),d.deal(),d.deal(),d.deal(),d.deal(),d.deal(),d.deal()};
    short[] test7 = {d.deal(),d.deal(),d.deal(),d.deal(),d.deal(),d.deal(),d.deal()};
    Hand h4 = new Hand(test4);
    Hand h5 = new Hand(test5);
    Hand h6 = new Hand(test6);
    Hand h7 = new Hand(test7);

    System.out.println(h4);
    System.out.println(h5);
    System.out.println(h6);
    System.out.println(h7);

    System.out.println();
    System.out.println(h4.compareTo(h4));
    System.out.println(h4.compareTo(h5));
    System.out.println(h4.compareTo(h6));
    System.out.println(h4.compareTo(h7));
    System.out.println();
    System.out.println(h5.compareTo(h4));
    System.out.println(h5.compareTo(h5));
    System.out.println(h5.compareTo(h6));
    System.out.println(h5.compareTo(h7));
    System.out.println();
    System.out.println(h6.compareTo(h4));
    System.out.println(h6.compareTo(h5));
    System.out.println(h6.compareTo(h6));
    System.out.println(h6.compareTo(h7));
    System.out.println();
    System.out.println(h7.compareTo(h4));
    System.out.println(h7.compareTo(h5));
    System.out.println(h7.compareTo(h6));
    System.out.println(h7.compareTo(h7));
  }

}

