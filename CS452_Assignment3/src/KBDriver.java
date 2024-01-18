/**Alexandra Emerson**/
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class KBDriver {
	private Sentence KB = new Sentence(); //AKA, the premises
	private Sentence newKB; //used when alpha equals one of the premises
	private int clauseCount = 0;
	private Sentence alpha;
	private Sentence notAlpha;
	private Sentence kbAndNotAlpha;
	private ArrayList<Clause> proofResolvents = new ArrayList<Clause>();
	private CNFConverter CNFConverter;
	private ParseTree parseTree;
	
	public static void main(String[] args) {
		KBDriver kbd = new KBDriver();
		if (args.length == 0) {
			kbd.startInterativeMode();
		}
		else{
			String fileName = args[0];
			kbd.readFile(fileName);
		}
	}
	
	private void readFile(String fileName) {
		CNFConverter = new CNFConverter();
		String line;
		ArrayList<String> commands = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			while((line = reader.readLine())!= null) {
				if (!line.equals("") && (line.charAt(0)!= '#')) {
					commands.add(line);	
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		for (String command : commands) {
			System.out.println("> " + command);
			String [] input = command.split(" ", 2);
			String option = input[0].strip().toLowerCase();
			String theRest = input[1].strip();
			
			if (option.equals("tellc")) tellC(theRest);
			
			else if (option.equals("print")) printClauses();
			
			else if (option.equals("ask")) ask(theRest);
			
			else if (option.equals("proof")) proof(theRest);
			
			else if (option.equals("cnf")) {
				String sentence = "(" + theRest + ")";
				String cnfRep = CNFConverter.convert(sentence);
				System.out.println(cnfRep);
			}
			else if (option.equals("parse")){
				parse(theRest);
			}
			
			
		}
	}
	
	/** startIterativeMode() is the main method that allows
	 * the user to input commands into the Knowledge Base
	 * 
	 */
	private void startInterativeMode() {
		CNFConverter = new CNFConverter();
		
		System.out.println("Welome to the Knowledge Base!");
		System.out.println("Please TELL or ASK me anything!");
		System.out.println("(type HELP for more information)");
		System.out.print("> ");
		Scanner scan = new Scanner(System.in);
		String input = scan.next().toLowerCase();
		
		while (!(input.equals("done") || input.equals("exit") || input.equals("quit"))) {
			if (input.equals("help")) printHelp();
			
			else if (input.contains("tellc")) {
				String s = scan.nextLine().strip(); //removes leading and trailing white space
				tellC(s);
			}
			
			else if (input.equals("print")) printClauses();
			
			else if (input.contains("ask")) {
				String query = scan.nextLine();
				ask(query);
			}
			else if (input.contains("proof")) {
			//prints a proof of <query> from the knowledge base, obtined via the **resolution refutation method**
			//CURRENTLY ONLY SUPPORTING LITERALS.
				String query = scan.next();
				proof(query);
			}
			else if (input.contains("parse")) {
				//prints the parse tree of the given <sentence> in (simplified) propositional logic.
				//NOT CURRENTLY SUPPORTED.
				String sentence = scan.next();
				parse(sentence);
			}
			else if (input.contains("cnf")) {
				//prints the conjunctive normal form representation of the given <sentence> in (simplified) propositional logic.
				//CURRENTLY ONLY SUPPORTING LITERALS WITH NEGATIONS AND PARENTHESES.
				String sentence = "(" + scan.next() + ")";
				String cnfRep = CNFConverter.convert(sentence);
				System.out.println(cnfRep);
				
			}
			else if (input.contains("tell")) {
				//Adds the clauses in the **CNF representation** of <sentence> to the knowledge base
				//NOT CURRENTLY SUPPORTED.
				String sentence = scan.nextLine();
			}
			else {
				System.out.println("Input not recognized. Please try again.");
				System.out.println("(type HELP for more information)");
			}
			System.out.print("> ");
			input = scan.next().toLowerCase();	
		}
		
		System.out.println("Thank you for using the Knowledge Base!");
	}
	
	/** tellC(...) adds the
	 * given string to the knowledge base.
	 * 
	 * @param s
	 */
	private void tellC(String s) {
		String [] litStrings = s.split("v");
		
		ArrayList<Literal> literals = new ArrayList<Literal>();
		
		for (String litString : litStrings) {
			litString = litString.replaceAll("\\s", ""); //removes all whitespace
			Literal literal = new Literal(litString);
			literals.add(literal);
		}		
		Clause clause = new Clause(literals);
		KB.addClause(clause);
	}
	
	/** printHelp() prints a list of supported commands
	 * along with brief descriptions of their behavior
	 */
	private void printHelp() {
		System.out.println("Recognized Commands:");
		System.out.println("  HELP: Prints this help message");
		System.out.println("  DONE/EXIT/QUIT: Ends the session");
		System.out.println("  TELLC <clause>: Adds <clause> to KB");
		System.out.println("  PRINT: Prints the clauses in KB");
		System.out.println("  ASK <query>: Determines if KB entails <query>");
		System.out.println("  PROOF <query>: Prints a proof of <query> from KB");
		System.out.println("  PARSE <sentence>: Prints a parse tree for <sentence>");
		System.out.println("  CNF <sentence>: Prints <sentence> in conjunctive normal form");
		System.out.println("  TELL <sentence>: Adds clauses from <sentence> in CNF to KB");
	}
	
	private void ask(String query) {
		alpha = new Sentence(query);
		alpha.stringRep = CNFConverter.convert(alpha.stringRep);
		alpha.createClauses();
		
		boolean result = PLResolution(KB, alpha);
		
		if(result == true) {
			System.out.println("Yes, KB entails " + query.strip());
		}
		else {
			System.out.println("No, KB does not entail " + query.strip());
		}
		
		if (proofResolvents != null) {
			proofResolvents.clear();
		}
		
		if (finalResolvents != null) {
			finalResolvents.clear();
		}
		if (kbAndNotAlpha.clauses != null) {
			kbAndNotAlpha.clauses.clear();
		}
		
		clauseCount = 0;
	}
	
	/** proof(...) is the main method for
	 * proving the given query with the resolution
	 * refutation method.
	 * 
	 * @param query
	 */
	private void proof(String query) {
		//if (alpha == null || !alpha.stringRep.equals(query.strip())) {
			alpha = new Sentence(query);
			alpha.stringRep = CNFConverter.convert(alpha.stringRep);
			alpha.createClauses();
		//}
	
		boolean result = PLResolution(KB, alpha);
		
		if(result == true) {
			fixResolvents(); //eliminate the unnecessary (non-contributing) clauses
			
			System.out.println("Proof:");
			for (Clause c : kbAndNotAlpha.clauses) {
				System.out.println(proofString(c) + "[" + c.type + "]");
			}
			
			int i = 0;
			for (i = 0; i <finalResolvents.size()-1; i++) {
				Clause c = finalResolvents.get(i);
				System.out.println(proofString(c) + "[Resolution on " + c.resOn + ": " + c.resOnClauses[0].count + ", " + c.resOnClauses[1].count + "]");
			}
			Clause c = finalResolvents.get(i);
			int spaceSize = 15 - c.stringRep.length();
			String space = "";
			for (int j = 0; j < spaceSize; j++) {
				space = space + " ";
			}
			System.out.println(" " + (c.count) + ". " + c.stringRep
			+ space + "[Resolution on " + c.resOn + ": " + c.resOnClauses[0].count + ", " + c.resOnClauses[1].count + "]");
			proofResolvents.clear();
			finalResolvents.clear();
			kbAndNotAlpha.clauses.clear();
		}
		else {
			System.out.println("No proof exists");
		}
		clauseCount = 0;
	}
	
	
	ArrayList<Clause> finalResolvents;
	
	/**
	 * fixResolvents() uses an auxiliary recursive
	 * method, as well as other algorithsm, to eliminate
	 * unnecessary/useless clauses from our proof. It keeps
	 * only the clauses that directly contribute to the goal.
	 */
	private void fixResolvents() {
		int newPremiseCount = kbAndNotAlpha.clauses.size()+1;
		Collections.reverse(proofResolvents);
		
		finalResolvents = new ArrayList<Clause>();
		Clause root = proofResolvents.get(0);
		finalResolvents.add(root);

		traverse(root);
		
		Collections.reverse(finalResolvents);
		for (int i = 0; i<finalResolvents.size(); i++) {
			Clause fR1 = finalResolvents.get(i);

			for (int j = i+1; j<finalResolvents.size(); j++) {
				Clause fR2 = finalResolvents.get(j);
				
				if (fR1.count == fR2.resOnClauses[0].count) {
					fR2.resOnClauses[0].count = newPremiseCount;
				}
				
				else if(fR1.count == fR2.resOnClauses[1].count) {
					fR2.resOnClauses[1].count = newPremiseCount;
				}
			}
			
			fR1.count = newPremiseCount;
			newPremiseCount++;
		}		
	}
	
	/** traverse(...) starts from the contradiction, and,
	 * using pre-order, right-to-left recursion, finds
	 * only the clauses that contribute directly to the goal,
	 * 
	 * @param temp
	 */
	private void traverse(Clause temp) {
		if (temp.resOnClauses!=null) {
			
			if (!(finalResolvents.contains(temp))){
				finalResolvents.add(temp);
			}
	
			traverse(temp.resOnClauses[1]);
			traverse(temp.resOnClauses[0]);		
		}
	}
	
	/** proofString is a string builder for when the
	 * proof is printed.
	 * 
	 * @param c
	 * @return
	 */
	private String proofString(Clause c) {
		int spaceSize = 15 - c.stringRep.replaceAll("[()]", "").length();
		String space = "";
		for (int j = 0; j < spaceSize; j++) {
			space = space + " ";
		}
		
		String str = " " + (c.count) + ". " + c.stringRep.replaceAll("[()]", "") + space;
		return str;
	}
	
	/** printClauses() prints all of the clauses
	 * currently in the Knowledge Base
	 * 
	 */
	private void printClauses() {
		for (Clause c : KB.clauses) {
			System.out.println(c.stringRep);
		}
	}
	
	/**
	 * Resolution Refutation method
	 */
	private boolean PLResolution(Sentence KB, Sentence alpha){
		ArrayList<Clause> clauses = createClauseSet(alpha);
		ArrayList<Clause> newClauses = new ArrayList<Clause>(); //empty set
		
		while (true) {
			for (int i = 0; i <clauses.size(); i++) {
				for (int j = i+1; j< clauses.size(); j++) {
					
					Clause ci = clauses.get(i);
					Clause cj = clauses.get(j);
	
					ArrayList<Clause> resolvents = PLResolve(ci, cj);
					for(Clause c : resolvents) {
						c.setResOnClauses(ci, cj);
						this.proofResolvents.add(c);
						
						if (c.stringRep.equals("()")) {
							return true;
						}
					}
					newClauses = union(newClauses, resolvents);
				}		
			}
			
			if (isSubset(newClauses, clauses)) {//if newClauses is a subset of clauses
				return false;
			}
			clauses = union(clauses, newClauses);
		}
	}
	
	/** PLResolve returns the set of all clauses that can be obtained
	 * by resolving complementary literals in its two input clauses.
	 * 
	 * @param ci
	 * @param cj
	 * @return
	 */
	private ArrayList<Clause> PLResolve(Clause ci, Clause cj){
		ArrayList<Clause> resolvents = new ArrayList<Clause>();
		ArrayList<Literal> clauseLiterals = new ArrayList<Literal>();
		
		for (Literal liti : ci.literals) {
			clauseLiterals.add(liti);
		}
		
		for (Literal litj : cj.literals) {
			clauseLiterals.add(litj);
		}
		
		int i = 0;
		while (i<clauseLiterals.size()-1) { //keep going while does not reach the last element
			Literal lit1 = clauseLiterals.get(i);	
			int j = i+1;
			
			while(j<clauseLiterals.size()) { //j is allowed to go to the end but i cannot
				Literal lit2 = clauseLiterals.get(j);
				
				if ((lit1.propSymbol.equals(lit2.propSymbol)) && hasOppositeNegation(lit1, lit2)){
					
					clauseLiterals.remove(lit1); //remove initial tautology
					clauseLiterals.remove(lit2);
					Clause c = new Clause(clauseLiterals); //create a clause out of the remaining literals
					
					if(!containsTautology(c.literals) && !isDuplicate(c)) { //only count if the clause doesn't contain a tautology/is a duplicate
						c.count = ++clauseCount;
						c.resOn = lit1.propSymbol;
						resolvents.add(c);
					}				
				}
				
				else if((lit1.negation + lit1.propSymbol).equals(lit2.negation + lit2.propSymbol)) { //if they are exactly equal
					clauseLiterals.remove(lit1); //factoring
					Clause c = new Clause(clauseLiterals);
				
					if(!containsTautology(c.literals) && !isDuplicate(c)) {
						c.count = ++clauseCount;
						resolvents.add(c);
					}	
				}
				//if they are different do nothing
				
				j++; //advance j while keeping i the same
			}
			i++;
		}
		return resolvents;
	}
	
	/**
	 * containsTautology(...) takes in a list of the literals of
	 * a clause, and checks for duplicate proposition symbols.
	 * @param clauseLiterals
	 * @return
	 */
	public boolean containsTautology(ArrayList<Literal> clauseLiterals) {
		int i = 0;
		while (i<clauseLiterals.size()-1) { //keep going while does not reach the last element
			
			Literal lit1 = clauseLiterals.get(i);	
			int j = i+1;
			
			while(j<clauseLiterals.size()) { //j is allowed to go to the end
				Literal lit2 = clauseLiterals.get(j);
				if(lit1.propSymbol.equals(lit2.propSymbol)) {
					return true;
				}
				
				j++;
			}
			i++;
		}
		return false;
	}
	
	/** isDuplicate checks the current clause
	 * 
	 * @param c
	 * @param clauses
	 * @return
	 */
	public boolean isDuplicate(Clause c) {
		
		for (Clause kbC : kbAndNotAlpha.clauses) {
			if (c.stringRepRaw.equals(kbC.stringRepRaw)) return true;
		}
		
		for (Clause aC : alpha.clauses) {
			if (c.stringRepRaw.equals(aC.stringRepRaw)) return true;
		}
		
		for (Clause prC : proofResolvents) {
			if (c.stringRepRaw.equals(prC.stringRepRaw)) return true;
		}
		
		return false;
	}
	
	/** hasOppositeNegation takes in two literals (assuming they have the same
	 * proposition symbols), and returns true if they have opposite negation.
	 * Returns false otherwise
	 * 
	 * @param l1
	 * @param l2
	 * @return a boolean
	 */
	private boolean hasOppositeNegation(Literal l1, Literal l2) {	
		
		if (l1.negated == true && l2.negated == false) {
			return true;
		}	
		else if (l1.negated == false && l2.negated == true) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/** isSubset() returns true if newClauses is a subset of clauses
	 * 
	 * 
	 * @param newClauses
	 * @param clauses
	 * @return
	 */
	private boolean isSubset(ArrayList<Clause> newClauses, ArrayList<Clause> clauses) {
		Set<String> ncStringReps = new HashSet<>();
		
		for (Clause nc : newClauses) {
			ncStringReps.add(nc.stringRep);
		}
		
		Set<String> cStringReps = new HashSet<>();
		
		for (Clause c : clauses) {
			cStringReps.add(c.stringRep);
		}
		
		boolean result = cStringReps.containsAll(ncStringReps);
		return result;
	}
	
	/** union takes in two sets s1 and s2 (lists) of Clauses,
	 * and add all (non-duplciated) elements of s2 to s1.
	 * 
	 * @param s1
	 * @param s2
	 * @return s1
	 */
	public ArrayList<Clause> union(ArrayList<Clause> s1, ArrayList<Clause> s2){
		
		ArrayList<String> s1Strings = new ArrayList<String>();
		for (Clause c : s1) {
			s1Strings.add(c.stringRepRaw);
		}
		
		for (Clause c : s2) {
			String cStringRep = c.stringRepRaw;
			
			if (!s1Strings.contains(cStringRep)) {
				s1.add(c);
			}
		}
		return s1;
	}
	
	
	/** createClauseSet creates a Sentence for kbAndNotAlpha,
	 * consisting of the CNF clauses of KB and ~Alpha. It then takes
	 * those clauses and puts them into a LinkedHashSet (to retain order)
	 * which is then returned.
	 * 
	 * @param alpha
	 * @return clauses (LinkedHashSet)
	 */
	private ArrayList<Clause> createClauseSet(Sentence alpha){
		kbAndNotAlpha = new Sentence();
		ArrayList<Clause> clauses = new ArrayList<Clause>();
		String kb = "";
		
		for (Clause kbClause : KB.clauses) { //add the KB's clauses to KbAndNotAlpha
			kbAndNotAlpha.addClause(kbClause);
		}
		
		checkIfInKB(alpha); //alters kbAndNotAlpha if one of the clauses in alpha is contained in the KB
		
		for (Clause clause : kbAndNotAlpha.clauses) {
			clause.type = "Premise";
			clause.count = ++clauseCount;
		}
		
		notAlpha = new Sentence("~(" + alpha.stringRep.replaceAll("\\s", "") + ")"); //ONLY SUPPORTING LITERALS
		notAlpha.stringRep = CNFConverter.convert(notAlpha.stringRep);
		notAlpha.createClauses(); //sets notAlpha up with its Clauses and Literals.
		
		for (Clause c: notAlpha.clauses) {
			c.type = "Negated Goal";
			c.count = ++clauseCount;
			kbAndNotAlpha.addClause(c);
		}
		
		for (Clause c : kbAndNotAlpha.clauses) {
			clauses.add(c);
		}
		
		return clauses;
	}
	
	/** checkIfInKB(...) alters kbAndNotAlpha if one of the 
	 * clauses in alpha is contained in kbAndNotAlpha.
	 * @param alpha
	 */
	private void checkIfInKB(Sentence alpha) {
		
		for (int i = 0; i<alpha.clauses.size(); i++) {
			Clause a = alpha.clauses.get(i);
			
			for (int j = 0; j < kbAndNotAlpha.clauses.size(); j++) {
				Clause c = kbAndNotAlpha.clauses.get(j);
				
				if (c.stringRepRaw.equals(a.stringRepRaw)) {
					kbAndNotAlpha.clauses.clear();
					kbAndNotAlpha.addClause(c);
				}
			}
		}
		
	}
	
	/** parse(...) - NOT CURRENTLY SUPPORTED
	 * 
	 * @param s
	 */
	private void parse(String s) {
		Sentence sentence = new Sentence(s);
		sentence.createClauses();
		parseTree = sentence.buildParseTree();
		System.out.println("Orig: [" + sentence.stringRep + "] " + parseTree.type + " [" + parseTree.subType + "]: " + "[" + sentence.stringRep + "]");
	}
}
