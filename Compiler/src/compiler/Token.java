/**
 * 
 * @author Ryan Bertsche
 */
package compiler;


/**
 * The Class Token is the object that is created when a new token is found during regex.  It holds all the important information
 * about the token, including its line number, position, what type of token it is based on the enumerated class, as well as the data that it holds.  
 */
public class Token 
{
	 
 	/** The type of token based on the enumerated field. */
 	TokenType type;
     
     /** The data is the actual value that is pulled from the source file when a token is created. */
     String data;
     
     /** The pos. this is the position on the line that the token is found*/
     int pos;
     
     /** The linNum is the line of the source file that the token is found. */
     int linNum;

     boolean idUsed;
     boolean idAssigned;

     String scope;

     /**
		 * Instantiates a new token.
		 *
		 * @param tType
		 *            the tokenType
		 * @param tData
		 *            the actual value of the token
		 * @param tLinNum
		 *            the line number the token was found
		 * @param tPos
		 *            the position on the line the number was found
		 */
     public Token(TokenType tType, String tData, int tLinNum, int tPos) 
     {
         type = tType;
         data = tData;
         pos = tPos;
         linNum = tLinNum;
          idUsed = false;
          idAssigned = false;
          scope = "";
     }
     
     /**
		 * Gets the position.
		 *
		 * @return the position
		 */
     public int getPosition()
     {
    	 return pos;
     }
     
     /**
		 * Gets the line number.
		 *
		 * @return the line
		 */
     public int getLine()
     {
    	 return linNum;
     }
     
     /**
		 * Gets the data is the actual value that is pulled from the source file
		 * when a token is created.
		 *
		 * @return the data is the actual value that is pulled from the source
		 *         file when a token is created
		 */
     public String getData()
     {
    	 return data;
     }
     
     /**
		 * Gets the token type.
		 *
		 * @return the token type
		 */
     public TokenType getTokenType()
     {
    	 return type;
     }

     /* toString formats the token in a nice readable way if you need to print out the value of a token
      * 
      */
     @Override
     public String toString() {
         return String.format("(%s %s line number: %s position: %s)", type.name(), data, linNum, pos);
     }


     public void setAssigned()
     {
          this.idAssigned = true;
     }

     public void setUsed()
     {
          this.idUsed = true;
     }

     public boolean isIdAssigned()
     {
          return idAssigned;
     }

     public boolean isIdUsed()
     {
          return idUsed;
     }

     public void setScope(String uuid)
     {
          scope = uuid;
     }

     public String getScope()
     {
          return scope;
     }

}
