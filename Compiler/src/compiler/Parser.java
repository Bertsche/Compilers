/**The parser class takes a list of tokens and runs them through the grammar.
 * It uses mutual recursion to match all the tokens to the grammar.  If a token doesn't match the grammar, an error is thrown.
 * If there are tokens left after the grammar finishes, an error is thrown.  If the list runs out of tokens before completion of grammar,
 * you guessed it, an error is thrown
 * @author Ryan Bertsche
 */
package compiler;

import java.util.ArrayList;

/**
 * The Class Parser.  This is the parser class
 */
public class Parser
{

	/** The token list. */
	ArrayList<Token> tokenList;

	/** The current position. This is the pointer that tracks where we are in the list of tokens */
	int currentPosition;

	/** CST Tree that is built from parsing*/
	Tree csTree;
	Tree asTree;
	/**
	 * Instantiates a new parser, taking in the token list made by the lexer.  It sets the position pointer to zero.  It then calls the start of the grammar
	 * and if you are competent enough to write valid code, you get a congratulations message.
	 *
	 * @param tokenList
	 *            the token list is the list of tokens, in order, generated by the lexer
	 * @throws Exception
	 *             the exception that is thrown whenever there is a parse error
	 */
	public Parser(ArrayList<Token> tokenList) throws Exception
	{
		csTree = new Tree();
		asTree = new Tree();
		
		this.tokenList = tokenList;
		currentPosition = 0;
		parseProgram();
		createAST(csTree.getRoot());
		asTree.repairAst();
		System.out.println("Congratulations, you successfully parsed with no errors");

	}

	//These are all the helper methods not directly part of the grammar recursion, all obnoxious commented with java docs

	/**
	 * Epsilon escape.  This magically method does absolutely nothing.  Java does not like else statements that do nothing,
	 * but it has no problem with methods that do nothing, so why not.
	 */
	private void epsilonEscape()
	{
		//Literally does absolutely nothing.  Just signifies the free exit of an epsilon transition in the grammar
	}

	/**
	 * Position incrementer.  Instead of just incrementing the pointer on the match, I wanted to do a little error checking first.
	 * if it is the end token , but not the last token in the list I throw an error. it should throw an error without this check, but you can
	 * never underestimate the evil of test cases.  The other check makes sure that incrementing the counter won't throw an index out of bounds exception.
	 * That means that the grammar didn't complete because a lack of tokens.
	 *
	 *
	 *
	 * @param expectedToken
	 *            the expected token is taken in just to do a final token check, and to help throw a good error
	 * @throws Exception
	 *             the exception
	 */
	private void positionIncrementer(TokenType expectedToken) throws Exception
	{
		if (expectedToken == TokenType.ENDCODE)
			finishParseVerification();
		else if((currentPosition + 1) >= tokenList.size())
			unexpectedEnd();
		else
			currentPosition++;
	}

	/**
	 * Unexpected end is called when there are no more tokens left to consume, but the grammar is still parsing.  Doesn't directly throw an error,
	 * but instead adds an error token to the end of the list so the parser will catch the error itself and give a nice percise error message.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void unexpectedEnd() throws Exception
	{
		tokenList.add(new Token(TokenType.ERROR, "Unexpected End", tokenList.get(currentPosition).getLine(), tokenList.get(currentPosition).getPosition()));
		currentPosition++;
	}

	/**
	 * Finish parse verification called when the end token is processed by the grammar, but there are tokens remaining in the list. That's not allowed, so ERROR
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void finishParseVerification() throws Exception
	{
		if(currentPosition != tokenList.size() - 1)
			System.out.println("Warning, there were characters after the ending $, and they were ignored");

	}

	/**
	 * Match is the class called when the grammar runs across a terminal.  This is where the general error checking occurs.  If the tokens match,
	 * the pointer moves to the next token, and the grammar continues to parse.  If not, the error thrower class is called in order to throw a nice
	 * easy to understand error.
	 *
	 * @param expectedToken
	 *            the expected token
	 * @throws Exception
	 *             the exception
	 */
	private void match(TokenType expectedToken) throws Exception
	{
		Token tempTHolder = tokenList.get(currentPosition);

		if(expectedToken == tempTHolder.getTokenType())
		{
			csTree.addLeafNode(tempTHolder);
			positionIncrementer(expectedToken);
		}
		else
		{
			errorThrower(expectedToken, tempTHolder);
		}

	}

	/**
	 * Error thrower that takes in a single expected token type.
	 *
	 * @param expected
	 *            the expected token type that the grammar believes it should be getting
	 * @param actual
	 *            the actual token that is next in the token stream
	 * @throws Exception
	 *             the exception
	 */
	private void errorThrower(TokenType expected, Token actual) throws Exception
	{
		String error = "PARSE ERROR at line " + actual.getLine() + ", Position " + actual.getPosition() +
							"\nExpected " + expected.name() + ", instead got " + actual.getTokenType().name();

		//Special case to catch error token added when there is an unexpected end to token stream.  Gives programmer a little extra info to help them.
		if(actual.getTokenType() == TokenType.ERROR)
			error = error + "  Unexpected end to token stream.";

		throw new Exception(error);
	}

	/**
	 * Error thrower for a list of expected types. Same method name as the other errorThrower, with different parameters.
	 * Comes in handy when there are multiple types that can be expected, but none of them are matched.
	 * For example, when a lookAhead happens, and none match, the user gets to see all the possible valid token types
	 *
	 * @param expected
	 *            the expected is the array of tokens that the grammar expected
	 * @param grammarLevel
	 *            the grammar level gives the user even more insight to what the grammar expects, just in case giving them every possible token wasn't clear enough.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void errorThrower(TokenType[] expected, String grammarLevel) throws Exception
	{
		Token actual = tokenList.get(currentPosition);
		String error = "PARSE ERROR at line " + actual.getLine() + ", Position " + actual.getPosition() + "\nExpected a(n) "
						+ grammarLevel + ", specifically one of the following tokens: ";

		for(TokenType ex : expected)
		{
			error = error + ex.name() + " or ";
		}
		error.replaceAll(" or $", ".");

		error = error + "\nInstead got " + actual.getTokenType().name();

		throw new Exception(error);
	}

	/**
	 * Look ahead for a list of tokens.  Makes it easy to match first sets when there is a non-terminal at the start of a production that needs to be looked ahead to.
	 * I could definitely make this return an integer for position instead of boolean to slightly optimize code, but it works the way it is,
	 * and its a little too late for project one.  Will most likely implement in next version
	 *
	 * @param expectedTokens
	 *            the expected tokens is a list of tokens, of which one should be present
	 * @return true, if successful and there is at least one
	 */
	private boolean lookAhead(TokenType[] expectedTokens)
	{
		for(TokenType expectedT : expectedTokens)
		{
			if(expectedT == (tokenList.get(currentPosition).getTokenType()))
				return true;
		}
		return false;
	}

	/**
	 * Look ahead for a single token, overloaded with single tokentype parameter.  returns true if there is a match
	 *
	 * @param expectedToken
	 *            the expected token
	 * @return true, if successful
	 */
	private boolean lookAhead(TokenType expectedToken)
	{
		if(expectedToken == (tokenList.get(currentPosition).getTokenType()))
			return true;
		return false;
	}

	//Mutually recursive grammar begins here

	/**
	 * Parses the program.  This is follows the grammar almost exactly.  I don't have the intestinal fortitude to comment every single
	 * parse statement beautifully, but I will comment any special cases or methods that deviate slightly from the grammar(Really just strings)
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void parseProgram() throws Exception
	{
		csTree.addBranchNode("PROGRAM");
		parseBlock();
		match(TokenType.ENDCODE);
		csTree.jumpToParent();
	}

	/**
	 * Parses the block.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void parseBlock() throws Exception
	{
		csTree.addBranchNode("BLOCK");
		match(TokenType.OPENBRACKET);
		parseStatementList();
		match(TokenType.CLOSEDBRACKET);
		csTree.jumpToParent();
	}

	/**
	 * Parses the statement list.  Uses array lookahead, and notice the wonderful epsilonEscape method call that does absolutely nothing
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void parseStatementList() throws Exception
	{
		csTree.addBranchNode("STATEMENT LIST");
		if(lookAhead(new TokenType[]{TokenType.PRINT, TokenType.IDENTIFIER, TokenType.TYPE, TokenType.WHILE, TokenType.IF, TokenType.OPENBRACKET}))
		{
			parseStatement();
			parseStatementList();
		}
		else
			epsilonEscape();

		csTree.jumpToParent();
	}

	/**
	 * Parses the statement.  Know at least one of these will match because it was checked by the previous method, and would
	 * have epsilon-escaped(<- Perfect title for a Backus-Naur prison break/buddy comedy movie) otherwise.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void parseStatement() throws Exception
	{
		csTree.addBranchNode("STATEMENT");
		if(lookAhead(TokenType.PRINT))
			parsePrintStatement();
		else if(lookAhead(TokenType.IDENTIFIER))
			parseAssignmentStatement();
		else if(lookAhead(TokenType.TYPE))
			parseVarDecl();
		else if(lookAhead(TokenType.WHILE))
			parseWhileStatement();
		else if(lookAhead(TokenType.IF))
			parseIfStatement();
		else if(lookAhead(TokenType.OPENBRACKET))
			parseBlock();
		csTree.jumpToParent();
	}

	/**
	 * Parses the print statement.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void parsePrintStatement() throws Exception
	{
		csTree.addBranchNode("PRINT STATEMENT");
		match(TokenType.PRINT);
		match(TokenType.OPENPAREN);
		parseExpr();
		match(TokenType.CLOSEPAREN);
		csTree.jumpToParent();
	}

	/**
	 * Parses the assignment statement.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void parseAssignmentStatement() throws Exception
	{
		csTree.addBranchNode("ASSIGNMENT STATEMENT");
		parseID();
		match(TokenType.ASSIGNMENT);
		parseExpr();
		csTree.jumpToParent();
	}

	/**
	 * Parses the var decl.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void parseVarDecl() throws Exception
	{
		csTree.addBranchNode("VAR DECL");
		parseType();
		parseID();
		csTree.jumpToParent();
	}

	/**
	 * Parses the while statement.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void parseWhileStatement() throws Exception
	{
		csTree.addBranchNode("WHILE STATEMENT");
		match(TokenType.WHILE);
		parseBooleanExpr();
		parseBlock();
		csTree.jumpToParent();
	}

	/**
	 * Parses the if statement.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void parseIfStatement() throws Exception
	{
		csTree.addBranchNode("IF STATEMENT");
		match(TokenType.IF);
		parseBooleanExpr();
		parseBlock();
		csTree.jumpToParent();
	}

	/**
	 * Parses the expr.  Also uses array lookahead, but then if-elses through each if true.  Would be replaced with faster version in ArrayLookead
	 * for arrays is updated.  Also uses the array error thrower for super awesome error messages
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void parseExpr() throws Exception
	{
		csTree.addBranchNode("EXPR");
		if(lookAhead(new TokenType[]{TokenType.DIGIT, TokenType.STRINGLITERAL, TokenType.OPENPAREN, TokenType.BOOLVAL, TokenType.IDENTIFIER}))
		{
			if(lookAhead(TokenType.DIGIT))
				parseIntExpr();
			else if(lookAhead(TokenType.STRINGLITERAL))
				parseStringExpr();
			else if(lookAhead(new TokenType[]{TokenType.OPENPAREN, TokenType.BOOLVAL}))
				parseBooleanExpr();
			else if(lookAhead(TokenType.IDENTIFIER))
				parseID();
		}
		else
			errorThrower(new TokenType[]{TokenType.DIGIT, TokenType.STRINGLITERAL, TokenType.OPENPAREN, TokenType.BOOLVAL, TokenType.IDENTIFIER}, "EXPR");
		csTree.jumpToParent();
	}

	/**
	 * Parses the int expr. Uses simple single lookahead because of the production starting with a terminal
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void parseIntExpr() throws Exception
	{
		csTree.addBranchNode("INT EXPR");
		parseDigit();
		if(lookAhead(TokenType.INTOP))
		{
			parseIntOP();
			parseExpr();
		}
		csTree.jumpToParent();
	}

	/**
	 * Parses the string expr.  Differs slightly from grammar because the lexer matches strings, instead of having to create special string building lexer
	 * nonsense.  Hopefully will still work for later projects, but can easily be changed if need be.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void parseStringExpr() throws Exception
	{
		csTree.addBranchNode("STRING EXPR");
		match(TokenType.STRINGLITERAL);
		csTree.jumpToParent();
	}

	/**
	 * Parses the boolean expr.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void parseBooleanExpr() throws Exception
	{
		csTree.addBranchNode("BOOLEAN EXPR");
		if(lookAhead(TokenType.OPENPAREN))
		{
			match(TokenType.OPENPAREN);
			parseExpr();
			parseBoolOp();
			parseExpr();
			match(TokenType.CLOSEPAREN);
		}
		else
			parseBoolVal();
		csTree.jumpToParent();
	}

	/**
	 * Parses the id.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void parseID() throws Exception
	{
		csTree.addBranchNode("ID");
		match(TokenType.IDENTIFIER);
		csTree.jumpToParent();
	}

	/**
	 * Parses the type.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void parseType() throws Exception
	{
		csTree.addBranchNode("TYPE");
		match(TokenType.TYPE);
		csTree.jumpToParent();
	}

	/**
	 * Parses the digit.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void parseDigit() throws Exception
	{
		csTree.addBranchNode("DIGIT");
		match(TokenType.DIGIT);
		csTree.jumpToParent();
	}

	/**
	 * Parses the bool op.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void parseBoolOp() throws Exception
	{
		csTree.addBranchNode("BOOL OP");
		match(TokenType.BOOLOP);
		csTree.jumpToParent();
	}

	/**
	 * Parses the bool val.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void parseBoolVal() throws Exception
	{
		csTree.addBranchNode("BOOL VAL");
		match(TokenType.BOOLVAL);
		csTree.jumpToParent();
	}

	/**
	 * Parses the int op.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void parseIntOP() throws Exception
	{
		csTree.addBranchNode("INT OP");
		match(TokenType.INTOP);
		csTree.jumpToParent();
	}
	
	
	//Create the AST

	/**
	 * Creates an ast with cool switch statemnets based on the possible cst nodes and what should be created for each of
	 * them.  Recursively decends cst, while the ast tree tracks the position the nodes are added to the cst
	 * @param csNode
	 */
	public void createAST(TreeNode csNode)
	{ 
		boolean jump = false;
		String gT = csNode.getGrammarType();
		switch(gT)
		{
		case "BLOCK":
			asTree.addBranchNode(gT);
			jump = true;
			break;
		case "PRINT STATEMENT":
			asTree.addBranchNode(gT);
			jump = true;
			break;
		case "VAR DECL":
			asTree.addBranchNode(gT);
			jump = true;
			break;
		case "IF STATEMENT":
			asTree.addBranchNode(gT);
			jump = true;
			break;
		case "WHILE STATEMENT":
			asTree.addBranchNode(gT);
			jump = true;
			break;
		case "ASSIGNMENT STATEMENT":
			asTree.addBranchNode(gT);
			jump = true;
			break;
		case "INT EXPR":
			asTree.addBranchNode(gT);
			jump = true;
			break;
		case "BOOLEAN EXPR":
			asTree.addBranchNode(gT);
			jump = true;
			break;
			//Leaf nodes are ones that hold tokens so they are added with a different constructor
			case "LEAF":
			Token tempToken = csNode.getToken();
			switch(tempToken.getTokenType().name())
			{
			case "DIGIT":
				asTree.addLeafNode(tempToken);
				break;
			case "TYPE":
				asTree.addLeafNode(tempToken);
				break;
			case "STRINGLITERAL":
				asTree.addLeafNode(tempToken);
				break;
			case "BOOLOP":
				asTree.addLeafNode(tempToken);
				break;
			case "BOOLVAL":
				asTree.addLeafNode(tempToken);
				break;
			case "IDENTIFIER":
				asTree.addLeafNode(tempToken);
			default:
				break;
			}
		default:
			break;
		}
		//recursion
		for(TreeNode child: csNode.getChildren())
		{
			//if(!child.isLeaf())
				createAST(child);
		}
		//If jump was necessary, because recursed was not a leaf, then the ast is jumped to parent
		if (jump)
			asTree.jumpToParent();
			
	}
	
	//Accessor methods to retrieve the CST and AST
	public Tree getAST()
	{
		return asTree;
	}
	
	public Tree getCST()
	{
		return csTree;
	}
	
	
	

}
