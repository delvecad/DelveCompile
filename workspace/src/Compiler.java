/*
 * CMPT 432 Compilers
 * (c) Antonio DelVecchio 2018
 */

import Lexer.*;

/*
 * The main point of entry for the compiler. This is where all phases will be called when they
 * are implemented. This is the java file that should be run from the terminal. To do so, run
 * >javac /path/to/Compiler.java
 * >java /path/to/Compiler < TestCases/testcase.txt
 */
public class Compiler {

	public static void main(String[] args) {

		// Get input file from command line
		String fileInput = Lexer.getFileInput();

		// Create tokens, print the results of lex, and continue into Parse
		Lexer.lex(fileInput.toString());

	}

}
