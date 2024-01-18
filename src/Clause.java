/**Alexandra Emerson**/
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Clause { //a clause is a disjunction of literals
	public ArrayList<Literal> literals = new ArrayList<Literal>();
	public String stringRep = "("; //the standard string rep is surounded by parenthesis
	public String stringRepRaw;
	public int count;
	public Clause[] resOnClauses;
	public String resOn = "";
	public String type = "";
	public boolean contributedToGoal = false;
	
	public Clause(ArrayList<Literal> literals) {
		sortLiterals(literals);
		createStringRep();
	}
	
	public Clause(String stringRep) {
		this.stringRep = stringRep;
		this.stringRepRaw = stringRep.strip().replaceAll("\\s", "").replaceAll("[()]", "");
	}
	
	public void createStringRep() {
		if (literals.size()==0) {
			stringRep = "()";
		}
		else {
			int i;
		for (i = 0; i < literals.size()-1; i++) {
			stringRep = stringRep + literals.get(i).negation + literals.get(i).propSymbol + " v ";
		}
		stringRep = stringRep + literals.get(i).negation + literals.get(i).propSymbol + ")";
		}
		
		stringRepRaw = stringRep.strip().replaceAll("\\s", "").replaceAll("[()]", "");
	}
	
	public void sortLiterals(ArrayList<Literal> literals) {
		Collections.sort(literals, Clause.ClauseLitComparator);
		this.literals = literals;
	}
	
	public static Comparator<Literal> ClauseLitComparator = new Comparator<Literal>() {
		
		public int compare(Literal l1, Literal l2) {
			String propSym1 = l1.propSymbol;
			String propSym2 = l2.propSymbol;
			
			//DO A SPECIAL CASE FOR 0 IF NEEDED!
			//Returning in ascending order
			
			return propSym1.compareTo(propSym2);
		}
	};
	
	/** createLiterals called only when we have converted a query taken in from System.in
	 * to CNF. The method takes the string representation of the Clause, and creates
	 * the literals from it.
	 */
	public void createLiterals() {
		String str = stringRep.replaceAll("[()]", "");
		String[] litArray = str.split("v");
		
		for (int i = 0; i<litArray.length; i++) {
			Literal lit = new Literal(litArray[i]);
			literals.add(lit);
		}
	}
	
	public void setResOnClauses(Clause c1, Clause c2) {
		resOnClauses = new Clause[] {c1, c2};
	}
		
	
}
