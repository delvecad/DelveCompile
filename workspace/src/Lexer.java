// Working title: DelVe
// Antonio DelVecchio 2018
// CMPT 432 - Compilers

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/*
 * This is the lexer for the compiler. The lexer takes an input from a text file and generate
 * an array list of tokens that will be passed down the chain in the following phases of compiling.
 */

public class Lexer {
	public static enum TokenType {

		/*
		 * The below indicates our grammar.
		 * 
		 * NOTE: These regular expressions are placed in order of precedence
		 * descending. The longest matches take precedence over
		 * shorter matches. For example, int would be processed
		 * as "int" -> VARTYPE rather than: 
		 * "i" -> [ID]
		 * "n" -> [ID]
		 * "t" -> [ID]
		 */
		
		DIGIT("[0-9]"),
		ADDITION("[+]"),
		STRING("\"([^\"]*)\""),
		QUOTE("\""),
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
		CHAR("a-z"),
		BOOLOP("(==)|(!=)"),
		ASSIGN("[=]"),
		WHITESPACE("\\s+"),
		EOP("[$]"),
		UNACCEPTED(".");
		
		
		public final String pattern;
		
		private TokenType(String pattern) {
		      this.pattern = pattern;
		}
		
	}
		
	
	
	
	/*
	 * Takes a string as input, ensures that each token is formatted correctly,
	 * and then returns an array list of accepted tokens.
	 * @param input This should be the string gotten from your text file.
	 * @return ArrayList<Token> This returns an array list of tokens.
	 */
	public static ArrayList<Token> lex(String input) {
		
		ArrayList<Token> tokens = new ArrayList<Token>();
		ArrayList<Warning> warnings = new ArrayList<Warning>();

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
				
				if (matcher.group(TokenType.UNACCEPTED.name()) != null) {
					int lineNum = getLine(input, matcher.start());
					
					warnings.add(new Warning(matcher.group(TokenType.UNACCEPTED.name()), lineNum));
					
					break;
				}
				
				// accept all other valid tokens
				else if (matcher.group(token.name()) != null) {
					
					int lineNum = getLine(input, matcher.start());
					
					tokens.add(new Token(token, matcher.group(token.name()), lineNum));
					
					continue;
				}
			}
		}
		
		ArrayList<Token> filteredTokens = filterList(tokens);
		checkForEOP(filteredTokens);
		
		System.out.println("\n" + "Lexing...");
		for (Token token : filteredTokens) {
			System.out.println(token);
		}
		
		if (warnings.isEmpty()) {
			System.out.println("\nLexing completed with 0 Warnings \n");
		}
		else {
			System.out.println();
			for (Warning warning : warnings) {
				System.out.println(warning);
			}
			System.out.println("\nLexing complete\n");
		}
		
		
		return filteredTokens;
	}
	
	
	
	
	
	
	/*
	 * This function takes the array list of tokens created by the lexer
	 * and filters the strings so that they are converted to charLists 
	 * between quotes, as per our grammar. This function acts as a final pass 
	 * at the array list before it is ready for output.
	 * @param tokens Accepts an array list of tokens
	 * @return ArrayList<Token> returns a newly formatted array list
	 */
	static ArrayList<Token> filterList (ArrayList<Token> tokens) {

		// get quote tokens and break them into character lists
		ArrayList<Token> charTokens = new ArrayList<Token>();

		for (Token token : tokens) {
			if (token.type == TokenType.STRING) {
				char[] charArray = token.data.toCharArray();

				//first index is a quote mark
				charTokens.add(tokens.indexOf(token), new Token(TokenType.QUOTE, Character.toString(charArray[0]), token.lineNum));

				for(int i = 1; i < charArray.length - 1 ; i++) {
					charTokens.add(tokens.indexOf(token) + i, new Token(TokenType.CHAR, Character.toString(charArray[i]), token.lineNum));
				}

				//last index is a quote mark (if quote gets recognized anywhere else, it's an error)
				charTokens.add(tokens.indexOf(token) + charArray.length - 1, new Token(TokenType.QUOTE, Character.toString(charArray[charArray.length - 1]), token.lineNum));
			}
			else
				charTokens.add(token);
		}	
		
		return charTokens;
	}
	
	
	
	
	
	/*
	 * This function finds the line breaks in the source program
	 * and calculates line numbers. It is used in particular for 
	 * assigning line numbers to corresponding tokens.
	 * @param data Accepts the string input from your text file.
	 * @param start Accepts a start index.
	 * @return Returns the line number of the content before the 
	 * 		   line break.
	 */
	static int getLine(String data, int start) {
	    
		int line = 0;
	    
	    Pattern pattern = Pattern.compile("[\r\n|\r|\n]");
	    Matcher matcher = pattern.matcher(data);
	    
	    matcher.region(0, start);
	    
	    while(matcher.find()) {
	    		line++;
	    }
	    
	    return(line);
	}
	
	
	
	
	
	/*
	 * This function checks to see if the input file contains an end of program token.
	 * It only checks and returns a boolean. It does nothing with the information.
	 */
	static ArrayList<Token> checkForEOP(ArrayList<Token> arrayList) {
		boolean foundEOP = false;
		
		for (Token token : arrayList) {
			if (token.type == TokenType.EOP) {
				foundEOP = true;
			}
		}
		if (foundEOP == false) {
			arrayList.add(new Token(TokenType.EOP, "$", arrayList.get(arrayList.size() - 1).lineNum));
			System.out.println("WARNING: Missing EOP token. Automatically inserted at end of program.");
		}
		return arrayList;
		
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		
		// Get input file from command line
		Scanner scanner = new Scanner(System.in);
		String fileInput = "";
		
		while (scanner.hasNextLine()) {
			fileInput = fileInput + "\n" + scanner.nextLine();
		}
		scanner.close();

		// Create tokens and print them
		lex(fileInput.toString());

	}

}
