/** The Tree class. It will be used as a container for the mathematical expression.
 *  multiplication and division are transformed in the end, to make sure that their 
 *  operands are leaves. That way those operations will be executed first
 * */

/**
 * @author samuel
 * 
 */

public class Tree {
	// val is either a number or an operator.
	// if it's an operator, left and right will be the left and right operands
	private String val;
	private Tree left;
	private Tree right;

	// This function transforms the expression into a tree, ignoring
	// multiplication and division.
	Tree strToTree1(String exp) {
		Tree tree = new Tree();
		if (getFirstOp1(exp) == '0') {
			tree.val = exp;
			tree.left = null;
			tree.right = null;
		} else {
			char operator = getFirstOp1(exp);
			if (operator == '+' || operator == '-') {
				tree.val = operator + "";
				tree.left = strToTree1(exp.substring(0, indexOfFirstOp1(exp)));
				tree.right = strToTree1(exp.substring(indexOfFirstOp1(exp) + 1));
			}
		}
		return tree;
	}

	// This function transforms an expression into a tree, ignoring addition and
	// subtraction
	Tree strToTree2(String exp) {
		Tree tree = new Tree();
		if (getFirstOp2(exp) == '0') {
			tree.val = exp;
			tree.left = null;
			tree.right = null;
		} else {
			char operator = getFirstOp2(exp);
			if (operator == '*' || operator == '/') {
				tree.val = operator + "";
				tree.left = strToTree2(exp.substring(0, indexOfFirstOp2(exp)));
				tree.right = strToTree2(exp.substring(indexOfFirstOp2(exp) + 1));
			}
		}
		return tree;
	}

	// this function will be applied after having applied (strToTree1())
	// It takes the result of strToTree1() and carries out the rest of the
	// transformation
	// (multiplication and division
	Tree secondTree(Tree tree) {

		char operator = getFirstOp(tree.val);
		if (operator == '*' || operator == '/') { // divide the expression
			tree.left = strToTree2(tree.val.substring(0,
					indexOfFirstOp(tree.val)));
			tree.right = strToTree2(tree.val
					.substring(indexOfFirstOp(tree.val) + 1));
			tree.val = operator + "";
		} else if (tree.val.equals("+") || tree.val.equals("-")) { // make sure
																	// to walk
																	// the rest
																	// of the
																	// tree
			tree.left = secondTree(tree.left);
			tree.right = secondTree(tree.right);
		}
		return tree;
	}

	// This function returns the first operator of a string expression.
	// For the minus operator, there are additional rules, to make sure that
	// only the binary
	// operator "-" is considered, not the unary one.
	char getFirstOp(String exp) {
		int i = 0;
		for (i = 1; i < exp.length(); i++) {

			if ((exp.charAt(i) == '+')
					|| (exp.charAt(i) == '-' && exp.charAt(i - 1) != '*' && exp
							.charAt(i - 1) != '/') || (exp.charAt(i) == '*')
					|| (exp.charAt(i) == '/'))
				return exp.charAt(i);
		}
		return '0';
	}

	int indexOfFirstOp(String exp) {
		int i = 0;
		for (i = 1; i < exp.length(); i++) {
			if ((exp.charAt(i) == '+')
					|| (exp.charAt(i) == '-' && exp.charAt(i - 1) != '*' && exp
							.charAt(i - 1) != '/') || (exp.charAt(i) == '*')
					|| (exp.charAt(i) == '/'))
				return i;
		}
		return -1;
	}

	// This function returns the first + or -.
	// It returns 0 if no + or - is found
	char getFirstOp1(String exp) {
		int i = 0;
		for (i = 1; i < exp.length(); i++) {
			if ((exp.charAt(i) == '+')
					|| (exp.charAt(i) == '-' && exp.charAt(i - 1) != '*' && exp
							.charAt(i - 1) != '/'))
				return exp.charAt(i);
		}
		return '0';
	}

	int indexOfFirstOp1(String exp) {
		int i = 0;
		for (i = 1; i < exp.length(); i++) {
			if ((exp.charAt(i) == '+')
					|| (exp.charAt(i) == '-' && exp.charAt(i - 1) != '*' && exp
							.charAt(i - 1) != '/'))
				return i;
		}
		return -1;
	}

	// This function returns the first * or /
	// It returns 0 if no * or / is found
	char getFirstOp2(String exp) {
		int i = 0;
		for (i = 1; i < exp.length(); i++) {
			if (exp.charAt(i) == '*' || exp.charAt(i) == '/')
				return exp.charAt(i);
		}
		return '0';
	}

	int indexOfFirstOp2(String exp) {
		int i = 0;
		for (i = 1; i < exp.length(); i++) {
			if (exp.charAt(i) == '*' || exp.charAt(i) == '/')
				return i;
		}
		return -1;
	}

	// Solves the tree.
	Double solve() {
		if (val.equals("*")) { // case of multiplication
			Double d1 = new Double(left.solve());
			Double d2 = new Double(right.solve());
			return d1 * d2;
		} else if (val.equals("/")) { // case of division
			Double d1 = new Double(left.solve());
			Double d2 = new Double(right.solve());
			return d1 / d2;
		} else if (val.equals("+")) { // case of addition
			Double d1 = new Double(left.solve());
			Double d2 = new Double(right.solve());
			return d1 + d2;
		} else if (val.equals("-")) { // case of subtraction
			Double d1 = new Double(left.solve());
			Double d2 = new Double(right.solve());
			return d1 - d2;
		} else { // case of NUMBER - simply return the number
			return new Double(val);
		}
	}

	// Empty constructor
	public Tree() {
		val = null;
		left = null;
		right = null;
	}

	// Constructor with a given mathematical expression
	public Tree(String exp) {
		// Remove white spaces
		String[] tokens = exp.split("\\s");
		int i;
		exp = "";
		for (i = 0; i < tokens.length; i++) {
			exp += tokens[i];
		}
		// Build the tree
		Tree tree = new Tree();
		tree = strToTree1(exp);
		tree = secondTree(tree);
		val = tree.val;
		left = tree.left;
		right = tree.right;
	}
}
