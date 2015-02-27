/**The lexer class builds an array list of tokens.
 * It first builds a large regex using the  enumerated class, then matches tokens and adds them to an array list.
 * The lexer class holds the main method and also runs the parser.
 * That means that you run the program by calling Lexer with the argument of the string of the file name
 * @author	Ryan Bertsche
 */

package compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



/**
 * The Lexer class that lexes the source file to find tokens, and report errors if invalid words are found.
 */
public class Lexer
{

    	/**
		 * Lex is the method that that handles the building of the token list.
		 *
		 * @param input
		 *            the input is a string version of the source file
		 * @return the array list of tokens built by the lexer
		 */
    	public static ArrayList<Token> lex(String input)
	    {
	       //Initiates the local variables
	        ArrayList<Token> tokens = new ArrayList<Token>();
	        int position = 1;
	        int lineNumber = 1;
	        boolean isEndToken = false;

	       /*This is the all important regex builder method.  It cycles through all the tokentypes, and builds regex groups
	        * Each group is separated by an or statement.  That way, the whole reqex is checked at once, and matched by groups
	        */
	        StringBuffer tokenPatternsBuffer = new StringBuffer();
	        for (TokenType tokenType : TokenType.values())
	            tokenPatternsBuffer.append(String.format("|(?<%s>%s)", tokenType.name(), tokenType.regexAsString));
	        Pattern tokenPatterns = Pattern.compile(new String(tokenPatternsBuffer.substring(1)));

	        // Begin matching tokens.  It keeps going as long as there is any match.  It matches by group
	        Matcher matcher = tokenPatterns.matcher(input);
	        while (matcher.find())
	        {
	        	/*
	        	 * It goes through every type of token and does the same thing.
	        	 * It makes a new token of the token type of the group matched and adds it to the token array
	        	 * IT also increments the position based on the length of the data
	        	 * I will not make a comment for each one, because I think you get the idea
	        	 */
	            if (matcher.group(TokenType.DIGIT.name()) != null)
	            {
	            	String t = matcher.group(TokenType.DIGIT.name());
	                tokens.add(new Token(TokenType.DIGIT, t, lineNumber, position));
	                position += t.length();

	            }
	            else if (matcher.group(TokenType.INTOP.name()) != null)
	            {
	            	String t =  matcher.group(TokenType.INTOP.name());
	                tokens.add(new Token(TokenType.INTOP, t, lineNumber, position));
	                position += t.length();
	            }
	            //Diifers from grammar, matches whole string, not just chars and quotes.It's my compiler so this is how it is.
	            else if(matcher.group(TokenType.STRINGLITERAL.name()) != null)
	            {
	            	String t = matcher.group(TokenType.STRINGLITERAL.name());
	                tokens.add(new Token(TokenType.STRINGLITERAL, t, lineNumber, position));
	                position += t.length();
	            }
	            else if(matcher.group(TokenType.IF.name()) != null)
	            {
	            	String t = matcher.group(TokenType.IF.name());
	                tokens.add(new Token(TokenType.IF, t, lineNumber, position));
	                position += t.length();
	            }
	            else if(matcher.group(TokenType.IDENTIFIER.name()) != null)
	            {
	            	String t =  matcher.group(TokenType.IDENTIFIER.name());
	                tokens.add(new Token(TokenType.IDENTIFIER,t, lineNumber, position));
	                position += t.length();
	            }
	            else if (matcher.group(TokenType.WHILE.name()) != null)
	            {
	            	String t = matcher.group(TokenType.WHILE.name());
	                tokens.add(new Token(TokenType.WHILE, t, lineNumber, position));
	                position += t.length();
	            }
	            else if (matcher.group(TokenType.PRINT.name()) != null)
	            {
	            	String t = matcher.group(TokenType.PRINT.name());
	                tokens.add(new Token(TokenType.PRINT, t, lineNumber, position));
	                position += t.length();
	            }
	            else if (matcher.group(TokenType.OPENBRACKET.name()) != null)
	            {
	            	String t = matcher.group(TokenType.OPENBRACKET.name());
	                tokens.add(new Token(TokenType.OPENBRACKET, t, lineNumber, position));
	                position += t.length();
	            }
	            else if (matcher.group(TokenType.CLOSEDBRACKET.name()) != null)
	            {
	            	String t = matcher.group(TokenType.CLOSEDBRACKET.name());
	                tokens.add(new Token(TokenType.CLOSEDBRACKET, t, lineNumber, position));
	                position += t.length();
	            }
	            else if (matcher.group(TokenType.OPENPAREN.name()) != null)
	            {
	            	String t = matcher.group(TokenType.OPENPAREN.name());
	                tokens.add(new Token(TokenType.OPENPAREN, t, lineNumber, position));
	                position += t.length();
	            }
	            else if (matcher.group(TokenType.CLOSEPAREN.name()) != null)
	            {
	            	String t = matcher.group(TokenType.CLOSEPAREN.name());
	                tokens.add(new Token(TokenType.CLOSEPAREN, t, lineNumber, position));
	                position += t.length();
	            }
	            else if (matcher.group(TokenType.TYPE.name()) != null)
	            {
	            	String t = matcher.group(TokenType.TYPE.name());
	                tokens.add(new Token(TokenType.TYPE, t, lineNumber, position));
	                position += t.length();
	            }
	            else if (matcher.group(TokenType.BOOLOP.name()) != null)
	            {
	            	String t = matcher.group(TokenType.BOOLOP.name());
	                tokens.add(new Token(TokenType.BOOLOP, t, lineNumber, position));
	                position += t.length();
	            }
	            else if (matcher.group(TokenType.BOOLVAL.name()) != null)
	            {
	            	String t = matcher.group(TokenType.BOOLVAL.name());
	                tokens.add(new Token(TokenType.BOOLVAL, t, lineNumber, position));
	                position += t.length();
	            }
	            else if (matcher.group(TokenType.ASSIGNMENT.name()) != null)
	            {
	            	String t = matcher.group(TokenType.ASSIGNMENT.name());
	                tokens.add(new Token(TokenType.ASSIGNMENT, t, lineNumber, position));
	            	position += t.length();
	            }
	            //This also sets endtoken boolean to true, for making sure there is $ at the end
	            else if (matcher.group(TokenType.ENDCODE.name()) != null)
	            {
	            	String t = matcher.group(TokenType.ENDCODE.name());
	                tokens.add(new Token(TokenType.ENDCODE, t, lineNumber, position));
	            	position += t.length();
	            	isEndToken = true;
	            }
	            //Really doesn't do anything but increment the position variable
	            else if (matcher.group(TokenType.WHITESPACE.name()) != null)
	            {
	            	String t = matcher.group(TokenType.WHITESPACE.name());
	            	position += t.length();
	            }
	            //Increments the newline variable, but doesn't create a token
	            else if (matcher.group(TokenType.NEWLINE.name()) != null)
	            {
	            	position = 1;
	            	lineNumber++;
	            }
	            //There is an error type that matches anything that isn;t matched by anything else.  This throws an error for unmatched token
	            else if (matcher.group(TokenType.ERROR.name()) != null)
	            {
	                throw new RuntimeException("You have a token that isn't valid at line number "+ lineNumber + "position: "position);
	            }

	        }
	        //This checks to see if there was an endtoken , if not it warns the user and adds it for them
	        if(!isEndToken)
	        {
	        	System.out.println("You forgot to end your code with a \"$\". I fixed it for you this time, but don't let it happen again!");
	        	tokens.add(new Token(TokenType.ENDCODE, "$", lineNumber, position));
	        }

	        return tokens;
	    }

	    /**
		 * This takes a string that is the filename of the file and turns it into a string that contains all the contents of the file.
		 *
		 * @param fileName
		 *            the file name is the string of the name of the file
		 * @return the string that is the whole source file turned into one string
		 */
    	private static String fileToString(String fileName)
	    {
	    	try
	    	{
	            File source = new File(fileName);
	            FileInputStream streamers = new FileInputStream(source);
	            byte[] buffer = new byte[streamers.available()];
	            streamers.read(buffer);
	            streamers.close();
	            String s = new String(buffer);
	            return s;
	        }
	    	catch (IOException e)
	    	{
	    		System.out.println("Something went horribly wrong when I tried to read your file.");
	            e.printStackTrace();
	        }
			return fileName;
	    }

	    /**
		 * The main method. The project should be run with the filename of the file as the argument.
		 *
		 * @param args
		 *            the arguments, there should only be one, and it should be the name of the file
		 * @throws Exception
		 *             the exception is thrown when there is a lex or parse error
		 */
    	public static void main(String[] args) throws Exception
	    {
	        String input = fileToString(args[0]);

	        // Create tokens and print them
	        ArrayList<Token> tokens = lex(input);

	        System.out.println("Congratulations, you successfully lexed with no errors");
	        new Parser(tokens);
	      /* Commented out code for printing successfully parsed tokens in order.  good for debugging, unnecessary for actual program
	       * for (Token t : tokens)
	            System.out.println(t);
	      */
	    }
}
