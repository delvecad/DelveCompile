/*
 * CMPT 432 Compilers
 * (c) Antonio DelVecchio 2018
 */

package Lexer;

/*
 * Class for representing data contained in a warning, i.e. the content of the 
 * warning token and the line number that it can be found on.
 */
public class Warning {

	public String data;
    public int lineNum;

    public Warning(String data, int lineNum) {
      this.data = data;
      this.lineNum = lineNum;
    }
	
	@Override
    public String toString() {
      return String.format("WARNING: Unrecognized token [%s] found on line " + lineNum + ". Token removed.", data);
    }
}
