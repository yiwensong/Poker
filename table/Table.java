package table;
import deck.*;
import player.*;
import java.util.Random;

/** Table is a class for sitting people down at the table.
 *  Each spot, except spot 0, will be filled with an AI player. The 0
 *  spot will be the human player.
 *  Table will keep track of the players, their current position (Blinds,
 *  Button, etc), their chip count, and statistics (when implemented). 
 */

/** TO DO.
 *  Side Pots
 *  Game flow
 *  Chip Leak
 */

public class Table {

  /* Players keep track of all the player stuff
   * The human player is always indexed at 0
   */
  private Player[] players;
  
  /* Index shown is the active player */
  private short activePlayer;

  /* Marks the person who made the first action (so it stops when we get there) */
  private short marker;

  /* When a player's index is true, that player's hand is still alive */
  private boolean[] playerInHand;

  /* Player VPI Number of times */
  private int[] playerVPIN;

  /* Player VPI this round */
  private boolean[] playerVPI;

  /* Number of hands played */
  private int handsPlayed;

  /* Number of players left in hand */
  private short hands;

  /* Bet to call */
  private int maxBet;

  /* Tracks bets from each player */
  private int[] playerBet;

  /* btn is a number that keeps track of which index in players the button
   * currently is on
   */
  private short btn;

  /* sb and bb are the blind amounts 
   * Other classes can access these amounts using the sb() and bb() methods.
   */
  private int sb;
  private int bb;

  /* this is pot amount
   */
  private int pot;

  /* tblCards are the cards on the table */
  private short[] tblCards = new short[5];

  /* deck is the current permutation of the deck.
   */
  private Deck deck = new Deck();

  /** Constructors - Please clean up constructors.
   *  The default constructor starts a table with 9 players, SB of 1, BB of 2,
   *  and a random player to start as button.
   *  Constructor with 1 input sets the SB to that argument, BB to twice of that.
   *  Constructor with 2 inputs sets SB to the first, and BB to the second.
   */
  public Table(){
    for(int i=0;i<5;i++){
      tblCards[i] = -1;
    }
    players = new Player[9];
    playerInHand = new boolean[9];
    playerVPI = new boolean[9];
    playerVPIN = new int[9];
    handsPlayed = 0;
    for(int i=1;i<9;i++){
      players[i] = new Player(false,2);
      playerInHand[i] = true;
      playerVPIN[i] = 0;
      playerVPI[i] = false;
    }
    playerVPI[0] = false;
    playerVPIN[0] = 0;
    playerInHand[0] = true;
    players[0] = new Player(true,2);
    Random rnjesus = new Random();
    btn = (short)rnjesus.nextInt(9);
    sb = 1;
    bb = sb*2;
    activePlayer = (short)((btn+3)%9);
    maxBet = 0;
    playerBet = new int[9];
    hands = 9;
  }

  public Table(int sb){
    for(int i=0;i<5;i++){
      tblCards[i] = -1;
    }
    players = new Player[9];
    playerInHand = new boolean[9];
    playerVPI = new boolean[9];
    playerVPIN = new int[9];
    handsPlayed = 0;
    for(int i=1;i<9;i++){
      players[i] = new Player(false,2*sb);
      playerInHand[i] = true;
      playerVPIN[i] = 0;
      playerVPI[i] = false;
    }
    playerVPI[0] = false;
    playerVPIN[0] = 0;
    playerInHand[0] = true;
    players[0] = new Player(true,2*sb);
    Random rnjesus = new Random();
    btn = (short)rnjesus.nextInt(9);
    this.sb = sb;
    bb = sb*2;
    activePlayer = (short)((btn+3)%9);
    maxBet = 0;
    playerBet = new int[9];
    hands = 9;
  }

  public Table(int sb,int bb){
    for(int i=0;i<5;i++){
      tblCards[i] = -1;
    }
    players = new Player[9];
    playerInHand = new boolean[9];
    playerVPI = new boolean[9];
    playerVPIN = new int[9];
    handsPlayed = 0;
    for(int i=1;i<9;i++){
      players[i] = new Player(false,bb);
      playerInHand[i] = true;
      playerVPIN[i] = 0;
      playerVPI[i] = false;
    }
    playerVPI[0] = false;
    playerVPIN[0] = 0;
    playerInHand[0] = true;
    players[0] = new Player(true,bb);
    Random rnjesus = new Random();
    btn = (short)rnjesus.nextInt(9);
    this.sb = sb;
    this.bb = bb;
    activePlayer = (short)((btn+3)%9);
    maxBet = 0;
    playerBet = new int[9];
    hands = 9;
  }

  /** Methods
   */

  // sb() and bb() return the table's small and big blind amounts
  public int sb() {
    return sb;
  }
  public int bb() {
    return bb;
  }
  
  // toCall(pn) returns the amount player n needs to call pot
  public int toCall(short pn) {
    return maxBet - playerBet[pn];
  }

  // tblCards() returns the cards on the table
  public short[] tblCards() {
    return tblCards;
  }

  // increaseBlinds() ints the BB and sets SB to the old BB
  private void increaseBlinds() {
    sb = bb;
    bb = 2*bb;
  }

  // next() goes to the next hand.
  // * rotates the button
  // * shuffles the deck
  // * resets table cards
  // * probably makes people pay blinds
  private void next() {
    btn = (short)((btn+1)%9);
    activePlayer = (short)((btn+3)%9);
    deck.shuffle();
    for(int i=0;i<9;i++){
      playerInHand[i] = true;
      players[i].resetHand();
      playerBet[i] = 0;
      if(players[i].chipCount() < bb) {
        players[i].rebuy(bb);
        System.out.println("Player " + i + " rebuys for " + (100*bb) + ".");
      }
    }
    for(int i=0;i<5;i++){
      tblCards[i] = -1;
    }
    maxBet = 0;
    pot = 0;
    hands = 9;
    marker = 9;
    for(int i=0;i<9;i++){
      if(playerVPI[i]){
        playerVPIN[i]++;
      }
      playerVPI[i] = false;
    }
    handsPlayed++;
    double[] pVPIP = this.playerVPIP();
    for(int i=0;i<9;i++){
      System.out.println("Player " + i + " VPIP is " + (int)(100*pVPIP[i]) + "%.");
    }
  }

  // playerVPIP() tells you what each player's VPIP is
  private double[] playerVPIP() {
    double[] ret = new double[9];
    for(int i=0;i<9;i++){
      ret[i] = (double)playerVPIN[i]/handsPlayed;
    }
    return ret;
  }

  // nextDeal() should deal the next card(s) or determine the winner
  private void nextDeal() {
    if(tblCards[0] == -1){
      flop();
    } else if(tblCards[3] == -1){
      turn();
    } else if(tblCards[4] == -1){
      river();
    } else {
      short winner = chooseWinner();
      payout(winner);
      next();
    }
  }

  // Makes players pay blinds
  // NOTE: Needs fixing later. Skips players when unable to pay blinds, but should
  // kick out of game, or rebuy.
  private void payBlinds() {
    short sbp = (short)((btn + 1)%9);
    short bbp = (short)((btn + 2)%9);
    pot = 0;
    try{
      players[sbp].toPot(sb);
      pot += sb;
      playerBet[sbp] = sb;
    } catch(InsufficientFundsException e){
      System.out.println("SB has insufficient funds. Blind Skipped.");
      playerInHand[sbp] = false;
      hands--;
    }
    try{
      players[bbp].toPot(bb);
      pot += bb;
      playerBet[bbp] = bb;
    } catch(InsufficientFundsException e){
      System.out.println("BB has insufficient funds. Blind Skipped.");
      playerInHand[bbp] = false;
      hands--;
    }
    maxBet = bb;
    activePlayer = (short)((btn+3)%9);
  }

  // Continues the game within a round
  private void cont() {
    int toCall = maxBet - playerBet[activePlayer];
    int bet;
    if(hands<2){
      payout(chooseWinner());
      next();
      return;
    }
    if(activePlayer==marker && (toCall==0 || players[activePlayer].chipCount()==0)){
      nextDeal();
      return;
    }
    if(!playerInHand[activePlayer] || players[activePlayer].chipCount()==0){
      // System.out.println("Player " + activePlayer + " is no longer in this hand.");
      activePlayer = (short)((activePlayer+1)%9);
      return;
    }
    if(toCall==0){
      bet = players[activePlayer].playerActionNB(this,activePlayer);
      if(bet > 0) {
        try{
          players[activePlayer].toPot(bet);
        } catch(InsufficientFundsException e){}
        maxBet += bet;
        playerBet[activePlayer] += bet;
        marker = activePlayer;
        pot += bet;
        System.out.println("Bet. Player " + activePlayer + " bets "
            + bet + ". Pot is " + pot + ".");
        playerVPI[activePlayer] = true;
      } else {
        if(marker==9){
          marker=activePlayer;
        }
        System.out.println("Check. Player " + activePlayer + " checks."); /*($" + 
            players[activePlayer].chipCount() + " - $" + toCall + " to call). Marker: "
            + marker);*/
      }
      activePlayer = (short)((activePlayer+1)%9);
    } else {
      bet = players[activePlayer].playerActionB(this,activePlayer);
      if(bet < toCall && bet != players[activePlayer].chipCount()){
        // player folds
        playerInHand[activePlayer] = false;
        hands--;
        System.out.println("Fold. Player " + activePlayer + " folds.");
      } else if(bet < toCall) {
        // we should do a side pot
        pot += bet;
        try{
          players[activePlayer].toPot(bet);
        } catch(InsufficientFundsException e){}
        playerVPI[activePlayer] = true;
        System.out.println("All in. Player " + activePlayer + " is all in. Pot is " + pot + ".");
      } else if(bet==toCall) {
        pot += bet;
        try{
          players[activePlayer].toPot(bet);
        } catch(InsufficientFundsException e){}
        playerVPI[activePlayer] = true;
        System.out.println("Call. Player " + activePlayer + " calls "
            + bet + ". Pot is " + pot + ".");
      } else {
        pot += bet;
        try{
          players[activePlayer].toPot(bet);
        } catch(InsufficientFundsException e){}
        playerBet[activePlayer] += bet;
        maxBet = playerBet[activePlayer];
        marker = activePlayer;
        playerVPI[activePlayer] = true;
        System.out.println("Raise. Player " + activePlayer + " raises to "
            + maxBet + ". Pot is " + pot + ".");
      }
      activePlayer = (short)((activePlayer+1)%9);
    }
  }

  // CHOOSES WINNER RANDOMLY PLS FIX
  private short chooseWinner() {
    // This will be implemented after rules are done
    Random rnjesus = new Random();
    short chosenone;
    do {
      chosenone = (short)rnjesus.nextInt(9);
    } while (!playerInHand[chosenone]);
    return chosenone;
  }

  // Gives pot to winner
  private void payout(short winner) {
    System.out.println("The winner of this hand is Player " + winner + "!");
    players[winner].rake(pot);
    pot = 0;
  }

  // deal() deals cards to each player
  private void deal() {
    deck.shuffle();
    for(int i=0;i<9;i++){
      players[i].deal(deck.deal());
    }
    for(int i=0;i<9;i++){
      players[i].deal(deck.deal());
    }
  }

  // flop() deals the flop
  private void flop() {
    for(int i=0;i<3;i++){
      tblCards[i] = deck.deal();
    }
    maxBet = 0;
    for(int i=0;i<9;i++){
      playerBet[i] = 0;
    }
    activePlayer = (short)((btn+1)%9);
    marker = 9;
    System.out.println("<><>FLOP: " + Deck.num2str(tblCards[0]) + " " +
        Deck.num2str(tblCards[1]) + " " + Deck.num2str(tblCards[2]) + ".<><>");
  }

  // turn() deals the turn
  private void turn() {
    tblCards[3] = deck.deal();
    maxBet = 0;
    for(int i=0;i<9;i++){
      playerBet[i] = 0;
    }
    activePlayer = (short)((btn+1)%9);
    marker = 9;
    System.out.println("<><>TURN: " + Deck.num2str(tblCards[3]) + ".<><>");
  }
    
  // river() deals the river
  private void river() {
    tblCards[4] = deck.deal();
    maxBet = 0;
    for(int i=0;i<9;i++){
      playerBet[i] = 0;
    }
    activePlayer = (short)((btn+1)%9);
    marker = 9;
    System.out.println("<><>RIVER: " + Deck.num2str(tblCards[4]) + ".<><>");
  }

  /** After this is the main() function.
   *  There are no more relevant methods after this comment.
   *  main() contains some tests to make sure the previous methods work.
   */
  public static void main(String[] args){
    Table tbl = new Table(1,2);
    int totalChips;

    for(int t=0;t<5*9;t++){
      tbl.payBlinds();
      tbl.deal();
      for(int i=0;i<9;i++){
        System.out.println(tbl.players[i].showCards() + " - chips: " + tbl.players[i].chipCount());
      }
      while(tbl.pot>0){
        tbl.cont();
      }
      totalChips = 0;
      for(int i=0;i<9;i++){
        totalChips += tbl.players[i].chipCount();
      }
      System.out.println("Chips on table: " + totalChips);
      if(t!=5*9-1) {
        System.out.println("\nNext Round...");
      } else {
        System.out.println("\nAfter a few rounds:");
      }
    }


    for(int i=0;i<9;i++){
      System.out.println("Player " + i + "'s chips: " + tbl.players[i].chipCount());
    }
    totalChips = 0;
    for(int i=0;i<9;i++){
      totalChips += tbl.players[i].chipCount();
    }
    System.out.println("Chips on table: " + totalChips);
  }
}
