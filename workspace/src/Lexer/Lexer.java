/*
 * CMPT 432 Compilers
 * (c) Antonio DelVecchio 2018
 */

package Lexer;

import Parser.Parse;
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
		ID("[a-z]"),
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
	 * 
	 * @param input This should be the string gotten from your text file.
	 * @return ArrayList<Token> This returns an array list of tokens.
	 */
	public static ArrayList<Token> lex(String input) {
		int programCount = 1;
		
		ArrayList<Token> tokens = new ArrayList<Token>();
		ArrayList<Error> errors = GlobalErrorList.getInstance().getArrayList();

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
					// DO NOTHING, thus ignoring whitespace
					continue;
				}
				
				// remove comments
				if (matcher.group(TokenType.COMMENT.name()) != null){
					//DO NOTHING, thus ignoring comments
					continue;
				}
				
				// Throw an error if there's an unterminated string
				if (matcher.group(TokenType.QUOTE.name()) != null){
					
					// get the line number of the unaccepted character
					int lineNum = getLine(input, matcher.start());
					
					// throw it in the error array
					errors.add(new Error(matcher.group(TokenType.QUOTE.name()), lineNum));

					
					break;
				}
				
				/*
				 * throw an error if the token matches none of the acceptable regular expressions
				 * and is caught by the last resort case, UNACCEPTED
				 */
				if (matcher.group(TokenType.UNACCEPTED.name()) != null) {
					
					// get the line number of the unaccepted character
					int lineNum = getLine(input, matcher.start());
					
					// throw it in the error array
					errors.add(new Error(matcher.group(TokenType.UNACCEPTED.name()), lineNum));
					
					// break out because there is nothing left to do when an error is caught
					break;
				}
				
				
				// accept all other valid tokens
				else if (matcher.group(token.name()) != null) {
					
					// get the line number
					int lineNum = getLine(input, matcher.start());
					
					// throw it in the tokens array
					tokens.add(new Token(token, matcher.group(token.name()), lineNum));
					
					continue;
				}
			}
            
		}
		
		
		
		/* 
		 * filter the list of accepted tokens so that strings are converted to char lists between quotes
		 * and any unaccepted characters between the quotes are caught and thrown as errors.
		 */
		ArrayList<Token> filteredTokens = filterList(tokens);
		
		
		
		/*
		 * check the filtered list to see if an EOP token is present, and place it at the end of the program 
		 * if it is absent
		 */
		boolean EOPFound = checkForEOP(filteredTokens);
		
		
		// Create an empty array that will hold the set of tokens being passed to each parse session
		ArrayList<Token> parseTokens = new ArrayList<Token>();
		
		// if there are no errors...
		if (errors.isEmpty()) {
			System.out.println("\n" + "Lexing program " + programCount + "...");
			
			// print out all of the found tokens
			for (Token token : filteredTokens) {
				
				// Here's what to do when we hit an EOP
				if(token.type == TokenType.EOP) {
					
					// output to the Lex log
					System.out.println(token);
					
					// add the EOP token to the array
					parseTokens.add(token);
					
					// output the success message
					System.out.println("\n Lexing completed successfully\n");
					
					// parse all of the tokens we found in this program
					Parse.parse(parseTokens, programCount);
					
					// clear out the array for the next program
					parseTokens.clear();
					
					// increment the program counter
					programCount++;
					
					// and here's what we do to compile a subsequent program if needed
					if(filteredTokens.indexOf(token) + 1 < filteredTokens.size()) {
						System.out.println("\n" + "Lexing program " + programCount + "...");
					}
				}
				// otherwise, throw every token you find in the array to be parsed
				else {
					
					// toss the token in
					parseTokens.add(token);
					
					// print it out to the Lex log
					System.out.println(token);
				}
			}
			
			// if there is no EOP token...
			if (EOPFound == false) {
				
				// let the user know that it was inserted for them
				System.out.println("\nWARNING: Missing EOP token. Automatically inserted at end of program.");
			}
		}
		
		// otherwise, there must be errors, so...
		else {
			
			// print out the first found error
			System.out.println(errors.get(0));
			
			
			// print the lex failure message
			System.out.println("\nLexing failed.\n");
			
			// return no array, so that no results get passed to further phases
			return null;
		}
		
		// if there were no errors, pass along that list of tokens
		return filteredTokens;
	}
	
	/*
	 * Accepts input from a text file scanned in from the command line
	 * 
	 * @return the text input as a string
	 */
	public static String getFileInput() {
		
		Scanner scanner = new Scanner(System.in);
		String fileInput = "";

		while (scanner.hasNextLine()) {
			fileInput = fileInput + "\n" + scanner.nextLine();
		}
		scanner.close();
		
		return fileInput;
	}
	
	
	/*
	 * This function takes the array list of tokens created by the lexer
	 * and filters the strings so that they are converted to charLists 
	 * between quotes, as per our grammar. This function acts as a final pass 
	 * at the array list before it is ready for output.
	 * 
	 * @param tokens Accepts an array list of tokens
	 * @return ArrayList<Token> returns a newly formatted array list
	 */
	public static ArrayList<Token> filterList (ArrayList<Token> tokens) {

		// get quote tokens and break them into character lists
		ArrayList<Token> charTokens = new ArrayList<Token>();


		/*
		 * Every token in the list is scanned. If it is a string, then it is first checked to 
		 * see if it is valid, then it is converted into a char list between quotes. If it is
		 * an invalid string containing invalid characters, i.e. '\' or '\n' then an error is
		 * sent to the error array, and lex fails.
		 */
		for (Token token : tokens) {
			if (token.type == TokenType.STRING) {
				char[] charArray = token.data.toCharArray();

				
				for(int i = 0; i < charArray.length; i++) {
					
					// if it's a character
					if(String.valueOf(charArray[i]).matches("[a-z ]")) {
						
						charTokens.add(new Token(TokenType.CHAR, Character.toString(charArray[i]), token.lineNum));
					}
					//if it's a quote
					else if (String.valueOf(charArray[i]).matches("\"")) {
						
						charTokens.add(new Token(TokenType.QUOTE, Character.toString(charArray[i]), token.lineNum));
					}
					else {
						
						//THIS SHOULD NEVER HAPPEN
					}
					
				}
			}
			else {
				
				// if the token is not a string, add it to the returned array
				charTokens.add(token);
			}
		}	
		
		return charTokens;
	}
	
	
	
	
	
	/*
	 * This function finds the line breaks in the source program
	 * and calculates line numbers. It is used in particular for 
	 * assigning line numbers to corresponding tokens.
	 * 
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
	 * If one is found, it returns true and does nothing else. If one is absent, then
	 * one is added at the end of the program and the method returns false.
	 * 
	 * @param an array list of tokens
	 * @return whether or not an EOP token is present
	 */
	static boolean checkForEOP(ArrayList<Token> arrayList) {
		boolean foundEOP = false;
		
		for (Token token : arrayList) {
			if (token.type == TokenType.EOP) {
				foundEOP = true;
			}
		}
		if (foundEOP == false) {
			arrayList.add(new Token(TokenType.EOP, "$", arrayList.get(arrayList.size() - 1).lineNum));
		}
		return foundEOP;
		
	}
	
}
