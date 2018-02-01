// Working title: DelVe
// Antonio DelVecchio 2018
// CMPT 432 - Compilers

import java.util.ArrayList;
import java.util.Scanner;
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
		 * The below indicates our grammar.
		 * 
		 * NOTE: They are ranked in order of precedence
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
		LBRACE("[{]"),
		RBRACE("[}]"),
		LPAREN("[(]"),
		RPAREN("[)]"),
		COMMENT("/\\*(.|[\\r\\n])*?\\*/"),
		BOOLVAL("(true)|(false)"),
		PRINT("print"),
		VARTYPE("((int)|(string)|(boolean))"),
		WHILE("while"),
		ELSE("else"),
		IF("if"),
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
				
				// remove whitespace
				if (matcher.group(TokenType.WHITESPACE.name()) != null) {

					continue;
				}
				
				//remove comments
				if (matcher.group(TokenType.COMMENT.name()) != null){
					
					continue;
				}
				
				// accept all other tokens
				else if (matcher.group(token.name()) != null) {

					tokens.add(new Token(token, matcher.group(token.name())));
					
					continue;
				}
			}
		}
		return tokens;
	}
	
	
	
	
	public static void main(String[] args) {

		// Get input file from command line
		Scanner scanner = new Scanner(System.in);
		String fileInput = "";
		
		while (scanner.hasNext()) {
			fileInput = fileInput + scanner.nextLine();
		}
		scanner.close();

		// Create tokens and print them
		ArrayList<Token> tokens = lex(fileInput);
		
		System.out.println("\n" + "Lexing...");
		for (Token token : tokens) {
			System.out.println(token);
		}

	}

}
