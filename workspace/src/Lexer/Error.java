/*
 * CMPT 432 Compilers
 * (c) Antonio DelVecchio 2018
 */

package Lexer;

/*
 * Class to represent lex errors. Contains the token that caused
 * the error and the line number of that error.
 */
public class Error {
	
	public String data;
    public int lineNum;

    public Error(String data, int lineNum) {
      this.data = data;
      this.lineNum = lineNum;
    }
	
	@Override
    public String toString() {
		if (data == "\"") {
			return String.format("ERROR: Unterminated string on line " + lineNum + ".");
		}
		else
			return String.format("ERROR: Unrecognized token [%s] found on line " + lineNum + ".", data);
    }
}
