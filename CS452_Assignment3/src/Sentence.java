/**Alexandra Emerson**/
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class Sentence { //a sentence expressed as a conjunction of clauses is said to be in CNF
	public ArrayList<Clause> clauses;
	public String stringRep;
	public String stringRepWithSpaces;
	
	ParseTree empty;
	ParseTree currentTree;
	Stack<ParseTree> treeStack;
	
	public Sentence() {	
		clauses = new ArrayList<Clause>();
	}
	
	public Sentence(String query) {
		stringRepWithSpaces = query;
		stringRep = query.replaceAll("\\s", "");
	}
	
	/** createClausesAndLiterals assumes that there are not yet set clauses
	 * and literals. This happens when a sentence is created only
	 * with a stringRep.
	 * 
	 * This method takes the stringRep of the Sentence and creates the ArrayList of
	 * clauses of instantiated Literals.
	 */
	public void createClauses() {
		clauses = new ArrayList<Clause>();
		String str = stringRep;
		String[] clauseArray = str.split("^");
		
		for (int i = 0; i<clauseArray.length; i++) {
			Clause c = new Clause(clauseArray[i]);
			c.createLiterals();
			clauses.add(c);
		}	
	}
	
	public void addClause(Clause clause) {
		clauses.add(clause);
	}
	
	public ParseTree buildParseTree() {
		treeStack = new Stack<ParseTree>();
		String[] tokens = stringRep.split("");
		empty = new ParseTree();
		treeStack.push(empty);
		currentTree = empty;
		
		for(int i = 0; i < tokens.length; i++) {
			String t = tokens[i];
			if (t.equals("(")) {
				currentTree.insertLeft();
				treeStack.push(currentTree);
				currentTree = currentTree.getLeftChild();
			}
			
			else if(t.equals("<") && tokens[i+1].equals("=") && tokens[i+2].equals(">")){ //bi-conditional
				
			}
			
			else if(t.equals("=") && tokens[i+1].equals(">")) { //conditional
				
			}
			
			else if(t.equals("^")) {
				
			}
			else if(t.equals("~")) {
				
			}
			
			else if (t.equals("v")) {
				
			}
			else if (t.equals(")")) {
				
			}
			
			else {
				currentTree.setRootValue(t);
				currentTree.type = "Unary";
				currentTree.subType = "symbol";
				currentTree = treeStack.pop(); //go back up to the parent
			}
		}
		
		return currentTree;
	}
}
