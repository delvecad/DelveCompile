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
		
		NEWLINE("[\r\n|\r|\n]"),
		DIGIT("[0-9]"),
		ADDITION("[+]"),
//		CHAR("(?<=\")(?:\\\\.|[^\"\\\\])*(?=\")"),
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
		WHITESPACE("\\s+"),
		EOP("[$]");
		
		
		
		public final String pattern;
		
		private TokenType(String pattern) {
		      this.pattern = pattern;
		}
		
	}
	
		
	
	// This function will lex the programs and return an arraylist of tokens
	public static ArrayList<Token> lex(String input) {
		boolean EOPTokenFound = false;
		int lineNum = 1;
		
		
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
				
				// remove comments
				if (matcher.group(TokenType.COMMENT.name()) != null){
					
					continue;
				}
				
				// ignore newline character in lexer output
				if (matcher.group(TokenType.NEWLINE.name()) != null){
					
					continue;
				}
				
				// accept all other valid tokens
				else if (matcher.group(token.name()) != null) {

					tokens.add(new Token(token, matcher.group(token.name()), getLine(input, matcher.start())));
					
					continue;
				}
			}
		}
		return tokens;
	}
	
	
	// Gets the line number of each token
	static int getLine(String data, int start) {
	    
		int line = 1;
	    
	    Pattern pattern = Pattern.compile("\n");
	    Matcher matcher = pattern.matcher(data);
	    
	    matcher.region(0, start);
	    
	    while(matcher.find()) {
	    		line++;
	    }
	    
	    return(line);
	}
	
	
	
	
	public static void main(String[] args) {
//		boolean EOPTokenFound = false;
//		int programCounter = 1;
		
		// Get input file from command line
		Scanner scanner = new Scanner(System.in);
		String fileInput = "";
		
		while (scanner.hasNextLine()) {
			fileInput = fileInput + "\n" + scanner.nextLine();
		}
		scanner.close();

		// Create tokens and print them
		ArrayList<Token> tokens = lex(fileInput.toString());
		
		System.out.println("\n" + "Lexing...");
		for (Token token : tokens) {
			System.out.println(token);
		}
		
		System.out.println("----------");
		System.out.println(fileInput);

	}

}
