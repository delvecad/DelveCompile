
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
