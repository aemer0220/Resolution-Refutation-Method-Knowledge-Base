/**Alexandra Emerson**/
public class ParseTree {
	public String str;
	public String label; //Orig, LHS, etc...
	public String type; //unary or binary
	public String subType; //symbol, (), ~, etc...
	
	public ParseTree sub;
	public ParseTree lhs;
	public ParseTree rhs;
	
	public ParseTree(String sentence) {
		this.str = sentence;
	}
	
	public ParseTree() {
		
	}
	
	public void insertLeft() {
		lhs = new ParseTree();
	}
	
	public void insertRight() {
		rhs = new ParseTree();
	}
	
	public ParseTree getLeftChild() {
		return lhs;
	}
	
	public ParseTree getRightChild() {
		return rhs;
	}
	
	public void setRootValue(String str) {
		this.str = str;
	}
}
