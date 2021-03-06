/*
 * CMPT 432 Compilers
 * (c) Antonio DelVecchio 2018
 */

package Lexer;

/*
 * Class to represent the required data contained in a token, i.e. the
 * type of token that it is (digit, id, etc.), the data (the actual content
 * of the token), and the line number that the token is found on in the 
 * source code.
 */

public class Token {
	public Lexer.TokenType type;
    public String data;
    public int lineNum;

    public Token(Lexer.TokenType type, String data, int lineNum) {
      this.type = type;
      this.data = data;
      this.lineNum = lineNum;
    }

    
    /*
     * This override of toString will spit out the tokens
     * like we like 'em
     */
    @Override
    public String toString() {
      return String.format("LEXER --> | %s [%s] on line " + lineNum, type.name(), data);
    }
}
