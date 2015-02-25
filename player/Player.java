package player;
import deck.*;
import table.*;
import java.util.Random;

/** Player is a class for all the AIs and the human player.
 *  (For now, everyone will play true randomly)
 *  Each instance of this class will have an identifier (whether it is human),
 *  an AI profile, and a chip count.
 *  More will be added later.
 */

public class Player {

  /* human is true if this player is human */
  private boolean human;
  /* we use a number to identify how aggressive the AI is */
  private int profile;
  /* chips is how much money the player has */
  private int chips;
  /* cards is what cards the player has */
  private short[] cards = new short[2];
  /* rnjesus is the default java RNG */
  private Random rnjesus = new Random();

  /** Constructors.
   *  Default constructor constructs a machine player with random profile and 200 chips.
   *  Constructors with 1 argument makes either human or machine.
   *  Constructor with 2 arguments takes (bool ishuman, bigblind) and sets up either a
   *  human or machine player with the appropriate amount of chips for that blind amount.
   */
  public Player(){
    human = false;
    profile = rnjesus.nextInt(9);
    chips = 200;
    cards[0] = -1;
    cards[1] = -1;
  }
  public Player(boolean hum){
    this();
    human = hum;
  }
  public Player(boolean hum,int bb){
    this();
    human = hum;
    chips = 100*bb;
  }

  /** Methods.
   *  Needs work.
   */
  
  // Gives the player a card to hold, like giving a kid a lollipop
  public void deal(short c) {
    if(cards[0] == -1){
      cards[0] = c;
    } else {
      cards[1] = c;
    }
  }

  // Takes the cards from a player, like taking candy from little kids
  public void resetHand() {
    cards[0] = -1;
    cards[1] = -1;
  }

  // subtracts bet to add to the pot
  public void toPot(int bet) throws InsufficientFundsException {
    if(chips<bet){
      throw new InsufficientFundsException();
    }
    chips -= bet;
  }

  // adds winnings from pot to chip count
  public void rake(int amt) {
    chips += amt;
  }

  // returns your chip count
  public int chipCount() {
    return chips;
  }

  // reveals your terrible bluff so the whole world knows
  public String showCards() {
    return Deck.num2str(cards[0]) + " " + Deck.num2str(cards[1]);
  }

  // returns cards
  public short[] revealHand() {
    return cards;
  }

  // rebuys for buyin
  public void rebuy(int bb) {
    chips = 100*bb;
  }

  /** playerActionNB() takes in argument Table.
   *  Based on the table state, the player will decide whether or not to bet. This
   *  function will return the bet, and a bet of 0 is a check.
   *  Also should check if player is human. If human, prompt a command line to enter values.
   *  Inputs are the table and the player number of this player at the table.
   */
  public int playerActionNB(Table tbl,short pn) {
    // Needs actions
    if(chips<=0){
      return 0;
    }
    int check = rnjesus.nextInt(10);
    if(check!=0){
      return 0;
    }
    return rnjesus.nextInt(chips);
  }

  /** playerActionB() takes in argument Table.
   *  Based on the table state, the player will decide whether to fold, call, or raise. This
   *  function will return the call or raise amount. An amount lower than the amount to call
   *  will be considered a fold, unless that amount is your chip count, in which case it is
   *  considered a shove.
   *  Also should check if player is human. If human, prompt a command line to enter values.
   *  Inputs are the table and the player number of this player at the table.
   */
  public int playerActionB(Table tbl,short pn) {
    // Needs actions
    if(tbl.toCall(pn) > chips) {
      int allIn = rnjesus.nextInt(4);
      if(allIn==1){
        return chips;
      } else {
        return 0;
      }
    }
    if(chips<=0){
      return 0;
    }
    int fold = rnjesus.nextInt(10);
    if(fold!=0){
      return 0;
    }
    return rnjesus.nextInt(chips);
  }
}

