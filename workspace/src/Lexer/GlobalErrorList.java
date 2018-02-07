package Lexer;

import java.util.ArrayList;

public class GlobalErrorList {
	private ArrayList<Error> errorList;

	private static GlobalErrorList instance;

	private GlobalErrorList(){
	    errorList = new ArrayList<Error>();
	}

	public static GlobalErrorList getInstance(){
	    if (instance == null){
	        instance = new GlobalErrorList();
	    }
	    return instance;
	}

	public ArrayList<Error> getArrayList() {
	    return errorList;
	}
}
