/**Alexandra Emerson**/
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;


public class CNFConverter {

	public CNFConverter() {
		
	}
	
	public String convert(String sentence) {
		String cnfString = sentence.strip();
		//Step 1: Eliminate <=>
		//String[]
		if (sentence.contains("<=>")) {
			cnfString = removeBiConditional(cnfString);
		}
		//Eliminate =>
		if (sentence.contains("=>")) {
			cnfString = removeConditional(cnfString);
		}		
		
		cnfString = moveTildeIn(cnfString);
		
		cnfString = applyDistributivity(cnfString);
		
		if (sentence.contains("(") || sentence.contains(")")) {
			cnfString = removeParentheses(cnfString);
		}
		
		return cnfString;	
	}
	
	/** removeBiConditional - NOT CURRENTLY SUPPORTED
	 * 
	 * @param cnfString
	 * @return
	 */
	private String removeBiConditional(String cnfString) {
		
		return cnfString;
	}
	
	/** removeConditional - NOT CURRENTLY SUPPORTED
	 * 
	 * @param cnfString
	 * @return
	 */
	private String removeConditional(String cnfString) {
		
		return cnfString;
	}
	
	/**
	 * 
	 * @param cnfString
	 * @return
	 */
	private String moveTildeIn(String cnfString) {
		Stack<String> negations = new Stack<String>();
		Stack<String> otherChars = new Stack<String>();
		for (int i = 0; i<cnfString.length(); i++) {
			String token = Character.toString(cnfString.charAt(i));
			if (token.equals("~")) {
				if(negations.isEmpty()) {
					negations.add(token);
				}
				else {
					negations.pop();
				}
			}
			else if(token.equals(")")) { //unwind the stack and build a string until we see a (, 
				String token2 = ")";		// then add it back to the stack
				token = otherChars.peek();
				while (!token.equals("(")) {
					token = otherChars.pop();
					token2 = token + token2;
					token = otherChars.peek();
				}		
				String negate = "";
				if (!negations.isEmpty()) {
					negate = negations.pop();
				}
				token = otherChars.pop();
				token2 = token + negate + token2;
				otherChars.add(token2);
			}		
			else {
				otherChars.add(token);
			}
		}
		String negate = "";
		if (!negations.isEmpty()) {
			negate = negations.pop();
		}
		cnfString = "";
		cnfString = negate;
		ArrayList<String> elements = new ArrayList<String>();
		while (!otherChars.isEmpty()) { //undwind the stack into an ArrayList, so we can reverse it and build a completed string
			elements.add(otherChars.pop());
		}
		Collections.reverse(elements);
	
		for (String element : elements) {
			cnfString = cnfString + element;
		}
		return cnfString;
	}

/**applyDistributivity - NOT CURRENTLY SUPPORTED
 * 
 * @param cnfString
 * @return
 */
private String applyDistributivity(String cnfString) {
		
		return cnfString;
}


private String removeParentheses(String cnfString) {
	Stack<String> chars = new Stack<String>();
	Stack<String> removed = new Stack<String>();	
	for (int i = 0; i<cnfString.length(); i++) {
		String token = Character.toString(cnfString.charAt(i));
		if (token.equals(")")) {
			String token2 = ")";
			token = chars.peek();
			if (token.equals("(")) {
				chars.pop();
				
			}
			else {
				while (!token.equals("(")) {
					token = chars.pop();
					token2 = token + token2;
					token = chars.peek();
				}			
				token = chars.pop();
				token2 = token + token2;
				removed.add(token2);
			}
		}
		else if(token.equals("^")) {
			removed.add(token);
		}
		else { //token is an open Parentheses "(" or a literal
			chars.add(token);
		}
	}
	ArrayList<String> elements = new ArrayList<String>();
	while (!removed.isEmpty()) { //undwind the stack into an ArrayList, so we can reverse it and build a completed string
		elements.add(removed.pop());
	}
	Collections.reverse(elements);
	cnfString = "";
	for (String element : elements) {
		cnfString = cnfString + element;
	}
	
	return cnfString;
}
}
