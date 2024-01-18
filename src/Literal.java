/**Alexandra Emerson**/
import java.util.ArrayList;
public class Literal { //each literal is a proposition symbol or a negated proposition symbol
	public String propSymbol;
	public String negation = "";
	public boolean negated; //not sure if needed
	
	public Literal(String propSymbol) {
		if (propSymbol.contains("~")) {
			this.propSymbol = propSymbol.replace("~", "");
			negation = "~";
			negated = true;	
		}
		else {
			this.propSymbol = propSymbol;
		}
	}
}
