
public class Token {
	public Lexer.TokenType type;
    public String data;

    public Token(Lexer.TokenType type, String data) {
      this.type = type;
      this.data = data;
    }

    
    /*
     * This override of toString will spit out the tokens
     * like we like 'em
     */
    @Override
    public String toString() {
      return String.format("LEXER --> | \"%s\" [%s]", type.name(), data);
    }
}
