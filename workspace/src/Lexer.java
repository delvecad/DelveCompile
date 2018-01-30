import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Lexer {
	
	/* Tokens are enumerated here so as to 
	 * define them with their explicit name,
	 * rather than with int identifiers
	 * or something like that
	 */
	public static enum TokenType {

		/*
		 * Boy is this neato. You can add to this list
		 * as you need, so you don't need to deal with
		 * conditions or giant, nested, if-else blocks.
		 * Fun fun fun.
		 * 
		 * NOTE: the below are ranked in order of precedence
		 * ascending. The longest matches take precedence over
		 * shorter matches. For example, int would be processed
		 * as "int" -> VARTYPE rather than: 
		 * "i" -> [ID]
		 * "n" -> [ID]
		 * "t" -> [ID]
		 */
		
		DIGIT("[0-9]"),
		ADDITION("[+]"),
		QUOTE("[\"]"),
		LPAREN("[{]"),
		RPAREN("[}]"),
		BOOLVAL("(true)|(false)"),
		PRINT("print"),
		VARTYPE("((int)|(string)|(boolean))"),
		WHILE("while"),
		ID("[A-Za-z]"),
		BOOLOP("(==)|(!=)"),
		ASSIGN("[=]"),
		WHITESPACE("[ ]"),
		EOP("[$]");
		
		
		
		public final String pattern;
		
		private TokenType(String pattern) {
		      this.pattern = pattern;
		}
		
	}
	
	
	
	// This function will lex the programs and return an arraylist of tokens
	public static ArrayList<Token> lex(String input) {

		ArrayList<Token> tokens = new ArrayList<Token>();


		StringBuffer buffer = new StringBuffer();

		for (TokenType tokenType : TokenType.values()) {
			buffer.append(String.format("|(?<%s>%s)", tokenType.name(), tokenType.pattern));
		}

		Pattern tokenPatterns = Pattern.compile(new String(buffer.substring(1)));

		
		Matcher matcher = tokenPatterns.matcher(input);

		while (matcher.find()) {
			for (TokenType token: TokenType.values()) {
				if (matcher.group(TokenType.WHITESPACE.name()) != null) {

					continue;

				}
				else if (matcher.group(token.name()) != null) {

					tokens.add(new Token(token, matcher.group(token.name())));
					continue;

				}
			}
		}
		return tokens;
	}
	
	
	
	
	public static void main(String[] args) {
		
		// We will accept an input file later. This is hardcoded for testing
		String input = "{intaintba=0b=0while(a!=3){print(a)while(b!=3){print(b)b=1+bif(b==2){print(\"there is no spoon\")}}b=0a=1+a}}$";
		
		// Create tokens and print them
	    ArrayList<Token> tokens = lex(input);
	    for (Token token : tokens)
	      System.out.println(token);
		
	}

}
