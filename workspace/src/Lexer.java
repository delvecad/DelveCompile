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
		EOP("[$]");
		
		
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
				
				// accept all other valid tokens
				else if (matcher.group(token.name()) != null) {
					
					int lineNum = getLine(input, matcher.start());
					
					tokens.add(new Token(token, matcher.group(token.name()), lineNum));
					
					continue;
				}
			}
		}
		
		ArrayList<Token> filteredTokens = filterList(tokens);
		
		System.out.println("\n" + "Lexing...");
		for (Token token : filteredTokens) {
			System.out.println(token);
		}
		
		System.out.println("----------\n");
		checkForEOP(filteredTokens);
		
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
	    
		int line = 1;
	    
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
			System.out.println("Warning: No EOP token found. EOP token inserted at end of program.");
		}
		return arrayList;
		
	}
	
	
	
	
	
	/*
	 * This function runs through the array list and checks for errors and warnings.
	 * It returns an Alert object, which contains the number of warnings and errors
	 * reported.
	 */
	static Alert statusReport(ArrayList<Token> arrayList) {
		int warnings = 0;
		int errors = 0;
		
		// Check for EOP
		
		// Check for unexpected characters
		
		return new Alert(warnings, errors);
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
		
		
		System.out.println("File Input:");
		System.out.println(fileInput);

	}

}
