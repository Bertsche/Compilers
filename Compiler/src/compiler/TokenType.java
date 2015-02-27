/**
 * The enumerated class serves the purpose of denoting all of the possible Tokens that the language can include.
 * Each group is named, its first field a string version of the regex that is used to search for it.
 * The second field is a String array of what the possible actual inputs could be to add clarity to error messages.  They are not currently used, but may
 * be impemented at a later time.    
 * @author	Ryan Bertsche
 */
package compiler;

public enum TokenType 
{
	//Order of these determines the order of precedence for the regex.  So order is extremley omportant
	//Also note that the string is matched fully and not as quotations and characters.  It seemed neater and esier, so thats how i did it
	//Also the error token at the end catches anything that matches no other regex, which produces an error token, which is caught by the lexer,and if it somehow squeaks through(It shouldn't)The parser would also be able to catch it.
        DIGIT("[0-9]", new String[]{"0","1","2","3","4","5","6","7","8","9"}), INTOP("[+]", new String[]{"+"}), WHITESPACE("[ ]|\t", new String[]{" "}), 
        NEWLINE("\n|\f|\r", new String[]{"\\n"}), WHILE("([w][h][i][l][e])", new String[]{"while"}), IF("[i][f]", new String[]{"if"}), 
        PRINT("[p][r][i][n][t]", new String[]{"print"}),STRINGLITERAL("\"([a-z]|[ ])*\"", new String[]{"\"Literally a string surrounded by quotes\""}),  
        OPENBRACKET("[{]", new String[]{"{"}),CLOSEDBRACKET("[}]", new String[]{"}"}), OPENPAREN("[(]", new String[]{"("}), 
        CLOSEPAREN("[)]", new String[]{")"}), TYPE("([s][t][r][i][n][g])|([b][o][o][l][e][a][n])|([i][n][t])", new String[]{"string", "boolean", "int"}), 
        BOOLOP("([=][=]|[!][=])", new String[]{"==", "!="}), BOOLVAL("([f][a][l][s][e])|([t][r][u][e])", new String[]{"false", "true"}), 
        IDENTIFIER("[a-z]", new String[]{"any lowercase letter in english alphabet"}),ASSIGNMENT("[=]", new String[]{"="}), ENDCODE("[$]", new String[]{"$"}), 
        ERROR(".+", new String[]{"ERROR"});

        public final String regexAsString;
        public final String[] literalString;

        private TokenType(String ras, String[] myString) {
            regexAsString = ras;
            literalString = myString;
        }
}


