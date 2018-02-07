/*
 * CMPT 432 Compilers
 * (c) Antonio DelVecchio 2018
 */

package Lexer;

import java.util.ArrayList;

/*
 * Singleton for a global array list of errors, so that various functions in Lexer
 * can communicate with the same array.
 */

public class GlobalErrorList {
	private ArrayList<Error> errorList;

	private static GlobalErrorList instance;

	
	private GlobalErrorList(){
	    errorList = new ArrayList<Error>();
	}
	
	/*
	 * Gets the current instance of the singleton
	 */
	public static GlobalErrorList getInstance(){
	    if (instance == null){
	        instance = new GlobalErrorList();
	    }
	    return instance;
	}

	/*
	 * Gets the current array list
	 */
	public ArrayList<Error> getArrayList() {
	    return errorList;
	}
}
