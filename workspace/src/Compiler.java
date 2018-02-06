import Lexer.*;
import java.util.ArrayList;

public class Compiler {

	public static void main(String[] args) {
		ArrayList<Token> lexTokens = new ArrayList<Token>();
		
		// Get input file from command line
		String fileInput = Lexer.getFileInput();

		// Create tokens, print the results of lex, and return the array list of tokens
		lexTokens = Lexer.lex(fileInput.toString());

	}

}
