
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
