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
		 */
		
		DIGIT("[0-9]"),
		ADDITION("[+]"),
		QUOTE("[\"]"),
		LPAREN("[{]"),
		RPAREN("[}]"),
		ID("[A-Za-z][A-Za-z0-9_]*"),
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
		String input = "{asdf = a}$ {{{{{{}}}}}}$\n";
		
		// Create tokens and print them
	    ArrayList<Token> tokens = lex(input);
	    for (Token token : tokens)
	      System.out.println(token);
		
	}

}
