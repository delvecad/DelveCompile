package Parser;

import java.util.ArrayList;
import Lexer.*;
import Lexer.Lexer.TokenType;

public class Parse {
	
	
	static Tree cst = new Tree();
	
	static int currentIndex = 0;
	static int errorCount = 0;
	static ArrayList<Token> workingCopy = new ArrayList<Token>();
	
	
	
	
	
	// Parse Program
	public static Tree parse(ArrayList<Token> lexedTokens, int programNum) {
		
		// Kill everything if Lexer didn't succeed
		if(lexedTokens == null) {
			System.out.println("PARSER: Skipped due to LEXER error(s)");
			System.out.println("CST: Skipped due to LEXER error(s)");
			return null;
		}
		
		// Refresh the tree between programs (for multi-program files)
		cst.reset();
		
		
		// Parse initiation message
		System.out.println("Parsing program " + programNum + "...");
		
		// Print parse() to the log, first log success message
		printSuccess("parse()");
		
		// Get that tree planted
		cst.addNode("Program", "branch");
		
		
		/*
		 *  Create a copy of the tokens array list so that we can remove 
		 *  consumed tokens as we go
		 */
		workingCopy = lexedTokens;
		
		
		// Parse block of the program
		parseBlock();
		
		
		// Check if program ends with EOP token
		match(workingCopy.get(currentIndex), Lexer.TokenType.EOP);
		
		
		if (errorCheck() == true) {
			
			return null;
		}
		
		// Print success message
		System.out.println("\n Parse completed successfully");
		
		// Print CST
		System.out.println("\nCST for program " + programNum + "\n" + cst.toString());
		
		// Return the CST created by the Parser
		return cst;
	}
	
	
	
	
	// Parse Block
	public static void parseBlock() {
		
		// add a branch to the tree
		cst.addNode("Block", "branch");
//		System.out.println(cst.toString());
		
		
		// Make sure the first token is a left brace
		printMatch(workingCopy.get(currentIndex), Lexer.TokenType.LBRACE, "parseBlock()");
			
		
		// Parse the statement list
		parseStatementList();
		
		// Make sure the last token is a right brace, and then print success (if successful)
		match(workingCopy.get(currentIndex), Lexer.TokenType.RBRACE);
		
		
		// return to parent 
		cst.endChildren();
	}
	
	
	
	
	
	// Parse StatementList
	public static void parseStatementList() {
		
		cst.addNode("StatementList", "branch");
		
		Token currentToken = workingCopy.get(currentIndex);
		Lexer.TokenType type = currentToken.type;
		
		
		printSuccess("parseStatementList()");
		
		
		// Statement lists begin with a statement, so check to make sure this is true here
		if (type == Lexer.TokenType.LBRACE || type == Lexer.TokenType.PRINT || type == Lexer.TokenType.IF || type == Lexer.TokenType.ID || type ==Lexer.TokenType.VARTYPE || type == Lexer.TokenType.WHILE) {
			
			parseStatement();
		}
		
		// return to parent
		cst.endChildren();
		
		
		// These get updated here to make sure we're getting current token types
		currentToken = workingCopy.get(currentIndex);
		type = currentToken.type;
		
		
		
		// Statement lists can also contain other statement lists. Check to see if there are any here
		if (type == Lexer.TokenType.LBRACE || type == Lexer.TokenType.PRINT || type == Lexer.TokenType.IF || type == Lexer.TokenType.ID || type ==Lexer.TokenType.VARTYPE || type == Lexer.TokenType.WHILE) {
			
			parseStatementList();
		}
		
		// Statement lists can also contain nothing at all. If nothing is found, do nothing
		else {
			
			// DO NOTHING
		}	

	}
	
	
	
	
	// Parse Statement
	public static void parseStatement() {
		
		cst.addNode("Statement", "branch");
		
		Token currentToken = workingCopy.get(currentIndex);
		Lexer.TokenType type = currentToken.type;
		
		
		// Parse the appropriate tokens based on what's accepted below
		if (type == Lexer.TokenType.LBRACE) {
			
			parseBlock();
		}
		else if (type == Lexer.TokenType.PRINT) {
			
			parsePrintStatement();
		}
		else if (type == Lexer.TokenType.IF) {
			
			parseIfStatement();
		}
		else if (type == Lexer.TokenType.ID) {
			
			parseAssignStatement();
		}
		else if (type == Lexer.TokenType.VARTYPE) {
			
			parseVarDecl();
		}
		else if (type == Lexer.TokenType.WHILE) {
			
			parseWhileStatement();
		}
		else {
			
			throwError(currentToken, "statement" );
		}
		
		cst.endChildren();
		
	}
	
	
	
	// Parse PrintStatement
	public static void parsePrintStatement() {
		
		cst.addNode("PrintStatement", "branch");
		
		
		// Check that the first token is a print statement and parse
		printMatch(workingCopy.get(currentIndex), Lexer.TokenType.PRINT, "parsePrintStatement()");
				
		// Check that the next token is an open paren
		match(workingCopy.get(currentIndex), Lexer.TokenType.LPAREN);
		
		
		// Make sure there's an expression all snugged up in there
		parseExpr();
				
				
		// Make sure the last token is a right paren, and then print success (if successful)
		match(workingCopy.get(currentIndex), Lexer.TokenType.RPAREN);
				
		
		// Return to parent
		cst.endChildren();
		
	}
	
	
	
	// Parse AssignmentStatement
	public static void parseAssignStatement() {
		
		cst.addNode("AssignStatement", "branch");
		
		Token currentToken = workingCopy.get(currentIndex);
		Lexer.TokenType type = currentToken.type;
		
		
		// check that first token is an id
		if(type == Lexer.TokenType.ID) {
			
			parseId();
		}
		else {
			
			throwError(currentToken, Lexer.TokenType.ID);
		}
		
		
		// make sure the token after that is an equal sign
		printMatch(workingCopy.get(currentIndex), Lexer.TokenType.ASSIGN, "parseAssignStatement()");
		
		// parse the rest of the expression
		parseExpr();
		
		
		cst.endChildren();
	}
	
	
	
	// Parse VarDecl
	public static void parseVarDecl() {
		
		cst.addNode("VarDecl", "branch");
		
		Token currentToken = workingCopy.get(currentIndex);
		Lexer.TokenType type = currentToken.type;
		
		// make sure we're starting with a valid variable type
		if (type == Lexer.TokenType.VARTYPE) {
			
			parseVartype();
		}
		else {
			
			throwError(currentToken, Lexer.TokenType.VARTYPE);
		}
		
		
		
		// update those values
		currentToken = workingCopy.get(currentIndex);
		type = currentToken.type;
		
		
		
		// then make sure we have a valid id
		if (type == Lexer.TokenType.ID) {
			
			parseId();
		}
		else {
			
			throwError(currentToken, Lexer.TokenType.ID);
		}
		
		
		// Return to parent
		cst.endChildren();
	}
	
	
	
	
	// Parse While Statement
	public static void parseWhileStatement() {
		
		cst.addNode("WhileStatement", "branch");
		
		Token currentToken = workingCopy.get(currentIndex);
		Lexer.TokenType whileToken = Lexer.TokenType.WHILE;
		
		
		// make sure first token is a while token
		printMatch(currentToken, whileToken, "parseIfStatement()");
		
		// make sure the next is a boolean expression
		parseBoolExpr();
		
		// and the next is a block
		parseBlock();
		
		
		// Return to parent
		cst.endChildren();
	}
	
	
	
	
	// Parse If Statement
	public static void parseIfStatement() {
		
		cst.addNode("IfStatement", "branch");
		
		Token currentToken = workingCopy.get(currentIndex);
		Lexer.TokenType ifToken = Lexer.TokenType.IF;
		
		
		// Make sure the first character is an if
		printMatch(currentToken, ifToken, "parseIfStatement()");
		
		// Consume the next parts of an if statement
		parseBoolExpr();
		
		parseBlock();
		
		
		// Return to parent
		cst.endChildren();
		
	}
	
	
	
	// Parse Expr
	public static void parseExpr() {
		
		cst.addNode("Expr", "branch");
		
		Token currentToken = workingCopy.get(currentIndex);
		Lexer.TokenType type = currentToken.type;
		
		
		
		// Parse the appropriate tokens based on what's accepted below
		if (type == Lexer.TokenType.DIGIT) {

			parseIntExpr();
		}
		else if (type == Lexer.TokenType.QUOTE) {

			parseStringExpr();
		}
		else if (type == Lexer.TokenType.LPAREN) {
			
			parseBoolExpr();
		}
		else if (type == Lexer.TokenType.BOOLVAL) {

			parseBoolExpr();
		}
		else if (type == Lexer.TokenType.ID) {
			
			parseId();
		}
		else {
			
			throwError(currentToken, "Expr");
		}
		
		
		// Return to parent
		cst.endChildren();
	}
	
	
	
	
	// Parse IntExpr
	public static void parseIntExpr() {
		
		cst.addNode("IntExpr", "branch");
		
		
		/*
		 * Here we need to keep our eye on two tokens, the current one and the one next in line.
		 * We can accept either a single digit, or a digit attached to an addition sign, so we
		 * have to look out for both. The variables below allow us to do that.
		 */
		Token currentToken = workingCopy.get(currentIndex);
		Token nextToken = workingCopy.get(currentIndex + 1);
		
		Lexer.TokenType currentType = currentToken.type;
		Lexer.TokenType nextType = nextToken.type;
		Lexer.TokenType digit = Lexer.TokenType.DIGIT;
		
		printSuccess("parseIntExpr()");
		
		
		// Parse the appropriate tokens based on what is accepted below
		if ((currentType == digit) && (nextType == Lexer.TokenType.ADDITION)) {
			
			parseDigit();
			
			parseAddition();
			
			parseExpr();
		}
		else if (currentType == digit) {
			
			parseDigit();
		}
		else {
			
			throwError(currentToken, digit);
		}
		
		
		// Return to parent
		cst.endChildren();
	}
	
	
	
	// Parse StringExpr
	public static void parseStringExpr() {
		
		cst.addNode("StringExpr", "branch");
		
		Token currentToken = workingCopy.get(currentIndex);
		Lexer.TokenType quote = Lexer.TokenType.QUOTE;
		
		
		// Make sure the first character is a quote
		printMatch(currentToken, quote, "parseStringExpr()");
		
		// dive into the char list
		parseCharList();
		
		// make sure the final character is a quote
		match(workingCopy.get(currentIndex), quote);
		
		
		// Return to parent
		cst.endChildren();
		
	}
	
	
	
	// Parse BoolExpr
	public static void parseBoolExpr() {
		/*
		 * There's gotta be a cleaner way to do this, like with a completion
		 * block in Swift. Look into passing methods into other methods
		 */
		
		cst.addNode("BoolExpr", "branch");
		
		Token currentToken = workingCopy.get(currentIndex);
		Lexer.TokenType type = currentToken.type;
		Lexer.TokenType lparen = Lexer.TokenType.LPAREN;
		Lexer.TokenType rparen = Lexer.TokenType.RPAREN;
		
		
		// Make sure the first token is a left paren
		if(type == lparen) {
			
			printMatch(currentToken, lparen, "parseBoolExpr()");
			
			parseExpr();
			
			parseBoolOp();
			
			parseExpr();
			
			// Make sure the last token is a right paren
			if(workingCopy.get(currentIndex).type == rparen) {
				match(workingCopy.get(currentIndex), rparen);
			}
			else {
				
				throwError(currentToken, rparen);
			}
		}
		// Or check the other first valid token
		else if(type == Lexer.TokenType.BOOLVAL) {
			
			parseBoolVal();
		}
		else {
			
			throwError(currentToken, Lexer.TokenType.BOOLVAL);
		}
		
		
		// Return to parent
		cst.endChildren();
	}
	
	
	
	
	// Parse CharList
	public static void parseCharList() {
		
		cst.addNode("CharList", "branch");
		
		Token currentToken = workingCopy.get(currentIndex);
		Lexer.TokenType type = currentToken.type;
		
		
		// Check to see if the current token is a char
		if (type == Lexer.TokenType.CHAR) {
			
			parseChar();
		}
		else {
			
			// DO NOTHING
		}
		
		cst.endChildren();
		
	}
	
	
	
	
	// Parse char
	public static void parseChar() {


		Token currentToken = workingCopy.get(currentIndex);
		Lexer.TokenType type = currentToken.type;

		// First, ensure that the next token is a character
		if (type == Lexer.TokenType.CHAR) {

			cst.addNode("Char", "branch");

			// print success message to the log
			printSuccess("parseChar()");

			consume(currentToken);


			// Return to parent HERE. Keeps the tree lookin' pretty
			cst.endChildren();


			parseChar();

		}
		// If it's not and it's a quote, break out
		else if (type == Lexer.TokenType.QUOTE) {

			// DO NOTHING and kick back to parseStringExpr()

		}
		else {

			throwError(currentToken, Lexer.TokenType.CHAR);
		}

	}
	
	
		
		
	
	// Parse Vartype
	public static void parseVartype() {
		
		cst.addNode("VarType", "branch");
		
		Token currentToken = workingCopy.get(currentIndex);
		
		
		printMatch(currentToken, Lexer.TokenType.VARTYPE, 
				"parseVarType()", "int | string | boolean");
		
		
		// Return to parent
		cst.endChildren();
	}
	
	
	
	
	
	// Parse Id
	public static void parseId() {
		
		cst.addNode("ID", "branch");
		
		Token currentToken = workingCopy.get(currentIndex);
		
		
		printMatch(currentToken, Lexer.TokenType.ID, "parseId()");
		
		
		// Return to parent
		cst.endChildren();
	}
	
	
	
	// Parse digit
	public static void parseDigit() {
		
		cst.addNode("Digit", "branch");
		
		Token currentToken = workingCopy.get(currentIndex);
		
		
		printMatch(currentToken, Lexer.TokenType.DIGIT, 
				"parseDigit()", "0-9");
		
		// Return to parent
				cst.endChildren();
	}
	
	
	
	// Parse boolop
	public static void parseBoolOp() {
		
		cst.addNode("BoolOp", "branch");
		
		Token currentToken = workingCopy.get(currentIndex);
		
		
		printMatch(currentToken, Lexer.TokenType.BOOLOP, 
				"parseBoolOp()", "== | !=");
		
		// Return to parent
				cst.endChildren();
		
	}
	
	
	
	// Parse boolval
	public static void parseBoolVal() {
		
		cst.addNode("BoolVal", "branch");
		
		Token currentToken = workingCopy.get(currentIndex);
		
		
		printMatch(currentToken, Lexer.TokenType.BOOLVAL, 
				"parseBoolVal()", "true | false");
		
		// Return to parent
				cst.endChildren();
		
	}
	
	
	
	// Parse Addition
	public static void parseAddition() {
		
		cst.addNode("IntOp", "branch");
		
		Token currentToken = workingCopy.get(currentIndex);
		
		
		printMatch(currentToken, Lexer.TokenType.ADDITION, "parseAddition()");
		
		// Return to parent
				cst.endChildren();
		
	}
	
	
	
	
	
	//-----------------------------CONSUMPTION METHODS--------------------------------------------
	
	
	
	
	// All-purpose token-matching and consuming method
	public static void match(Token currentToken, Lexer.TokenType expectedType) {
		if(check(currentToken.type, expectedType)) {
			consume(currentToken);
		}
		else {
			throwError(currentToken, expectedType);
		}
	}
	
	
	// Same as above, but it prints out a success message when it completes successfully
	public static void printMatch(Token currentToken, Lexer.TokenType expectedType, String message) {
		if(check(currentToken.type, expectedType)) {
			
			consumeAndPrint(currentToken, message);
		}
		else {
			throwError(currentToken, expectedType);
		}
	}
	
	
	// Overloads above, allowing you to add a custom error message on failure
	public static void printMatch(Token currentToken, Lexer.TokenType expectedType, String message, String customError) {
		if(check(currentToken.type, expectedType)) {
				
			consumeAndPrint(currentToken, message);
		}
		else {
			throwError(currentToken, expectedType);
		}
	}
	
	
	/*
	 *  Checks to see if the current token matches the expected token
	 *  Returns true if they match, returns false if not.
	 */
	public static boolean check(TokenType currentType, Lexer.TokenType expectedType) {
		if (currentType.equals(expectedType)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	
	/* 
	 * Takes the token literal being consumed and the message to be output,
	 * then outputs the message to the log, adds a leaf node to the CST,
	 * and iterates to the next token in the array list.
	 */
	public static void consume(Token token) {

		// add the EOP token to the CST
		cst.addNode(token.data, "Leaf");

		// remove the current index from the working copy
		workingCopy.remove(currentIndex);
	}
	
	
	
	/*
	 * Does the same as above, except it also prints a success message to the log
	 * in the form of a 'PARSER: ...' message.
	 */
	public static void consumeAndPrint(Token token, String message) {
		
		// print success message to the log
		printSuccess(message);
		
		// add the EOP token to the CST
		cst.addNode(token.data, "leaf");

		// remove the current index from the working copy, thus incrementing by 1 as well
		workingCopy.remove(currentIndex);
	}
	
	
	
	public static void printSuccess(String message) {
		
		// Print success message to the log
		System.out.println("PARSER --> | " + message);	
	}
	
	
	
	
	
	
	
	//-----------------------------ERROR METHODS--------------------------------------------------

	
	
	
	
	// Spits out a verbose error message when an unexpected token is found
	public static void throwError(Token currentToken, Lexer.TokenType expectedType) {
		
		// Print out the error message
		System.out.println("ERROR: Expected [" + expectedType + "] got [" + currentToken.type + "]" + " with value " + currentToken.data + " on line " + currentToken.lineNum);
		
		// sets error flag to true, killing the parser
		errorCount++;
	}
	
	
	
	// overloads the above and takes in a string argument
	public static void throwError(Token currentToken, String expectedType) { 
		
		// Print out the error message
		System.out.println("ERROR: Expected [" + expectedType + "] got [" + currentToken.type + "]" + " with value " + currentToken.data + " on line " + currentToken.lineNum);
				
		// sets error flag to true, killing the parser
		errorCount++;
	}
	
	
	
	// checks to see if errors have been found before proceeding
	public static boolean errorCheck() {
		if (errorCount > 0) {
			
			System.out.println("\n Parser failed with " + errorCount + " error(s)");
			System.out.println("\n CST skipped due to Parse fail \n");
			
			return true;
		}
		return false;
	}
	
	
	
}
