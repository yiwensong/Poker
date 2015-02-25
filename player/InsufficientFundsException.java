package player;

/** InsufficientFundsException is an exception thrown by Player.toPot().
 *  This happens when the player tries to make a bet that is larger than his chip count.
 */
public class InsufficientFundsException extends Exception {

  public InsufficientFundsException(){
  }

}
