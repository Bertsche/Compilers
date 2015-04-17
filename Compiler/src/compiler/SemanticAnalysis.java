package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This is the class where all the semantic analysis takes place, including building scope tables, into a scope table tree.
 * Also all forms of semamntic analysis and generates dot format for st display
 * @author Ryan Bertsche
 *
 * */
public class SemanticAnalysis
{
    Tree ast;
    SymbolTableTree stt;
    int scope = 0;

    /**
     *Constructor for the Semantic analysis that takes in a Abstract Syntax Tree. All the methods that need to be
     * called to build symbol table tree and analyze the semantics are called by the constructor
     * @param ast is the Abstract Syntax Tree generated after parsing that is used for all semantic analysis
     * @throws Exception
     */
    public SemanticAnalysis(Tree ast) throws Exception {
        this.ast = ast;
        stt = new SymbolTableTree();
        this.buildSymbolTree(ast.getRoot());
        this.checkForWarnings(stt.getRoot());
    }

    /**
     * This is the recursive function that builds the symbol table tree. It creates new scope at each block, and adds
     * new symbol at var decl. Anything else calls the helper calss getIdentifier
     * @param tn is the ast TreeNode
     * @throws Exception
     */
    public void buildSymbolTree(TreeNode tn) throws Exception {
        String gT = tn.getGrammarType();
        ArrayList<TreeNode> children= tn.getChildren();

        switch(gT)
        {
            case "BLOCK":
                System.out.println("Start of scope " + scope);
                scope++;
                stt.addNode();
                for(TreeNode child: children)
                    buildSymbolTree(child);
                if(! stt.isRoot()) {
                    stt.jumpToParent();
                    scope--;
                    System.out.println("Back to scope:  " + (scope-1));
                }
                break;
            case "VAR DECL":
                Character k = children.get(1).getToken().getData().charAt(0);
                Token v = children.get(0).getToken();
                stt.addSymbol(k, v);
                System.out.println("To scope " + (scope-1));
                break;
            default:
                typeOf(tn);
                for(TreeNode child:children) {
                    if (!child.isLeaf())
                        buildSymbolTree(child);
                }


        }
    }

    /**
     * This is a helper class that takes in an identifier and returns the type token from the symbol table if it exists
     * in the current scope or any parent scope.  If it does not exist, an error is thrown.  This get called when type
     * checking during symbol table building to make sure a symbol isn't used, then declared later in the same scope, which
     * would be illegal
     * @param tr is the AST treenode of the identifier that is being searched for in the symbol table
     * @return Token that matches the identifier
     * @throws Exception if the identifier is nowhere in any valid symbol table, undeclared identifier
     */
    private Token getIdentifier(TreeNode tr) throws Exception {
        Token t = stt.findIdentifier(tr.getToken().getData().charAt(0), stt.getCurrent());
        if (t == null)
        {
            Token errorAtToken = tr.getToken();
            throw new Exception("Error : There is an undeclared identifier: " + errorAtToken.getData() + ", at Line: " + errorAtToken.getLine() + ", Position: " + errorAtToken.getPosition());
        }
        return t;
    }

    /**
     * This is the helper method that gets called by the build symbol tree class.  It gets called when a node is not a block or vardecl.
     * It is capable of finding type mismatches, and sets used and declared flags on the tokens to be used later for warnings
     * @param tn is the current AST node that is supplied by the symbol table building method.  It is recursive, so it returns
     *           a string of the type, so type checking comes up the tree, so all necessary nodes have a type
     * @return String
     * @throws Exception
     */
    private String typeOf(TreeNode tn) throws Exception {
        String gT = tn.getGrammarType();
        ArrayList<TreeNode> cList = tn.getChildren();
        String c1;
        String c2;
        switch(gT)
        {
            case "BOOLEAN EXPR":

                c1 = typeOf(cList.get(0));
                c2 = typeOf(cList.get(2));
                if(c1.equals(c2))
                    return "boolean";
                else
                    throw new Exception("There is a type mismatch at Line: " + cList.get(2).getToken().getLine() + ", Position: " + cList.get(2).getToken().getPosition() + ". Expected type: " + c1 + ". Received type: " + c2);

            case "ASSIGNMENT STATEMENT":
               Token  assignToken = getIdentifier(cList.get(0));
                c1 =  assignToken.getData().toLowerCase();
                c2 = typeOf(cList.get(1));
                if(c1.equals(c2)) {
                   assignToken.setAssigned();

                    return "";
                }
                else
                    throw new Exception("There is a type mismatch at Line: " + cList.get(1).getToken().getLine() + ", Position: " + cList.get(1).getToken().getPosition() + ". Expected type: " + c1 + ". Received type: " + c2);

            case "INT EXPR":
                c1 = typeOf(cList.get(0));
                c2 = typeOf(cList.get(1));
                if(c1.equals(c2))
                    return "int";
                else
                   throw new Exception("There is a type mismatch at Line: " + cList.get(1).getToken().getLine() + ", Position: " + cList.get(1).getToken().getPosition() + ". Expected type: " + c1 + ". Received type: " + c2);

            case "PRINT STATEMENT":
                c1 = typeOf(cList.get(0));
                return "";
            case "LEAF":
                Token t = tn.getToken();
                switch(t.getTokenType().name())
                {
                    case "DIGIT":
                        return "int";
                    case "STRINGLITERAL":
                        return "string";
                    case "BOOLVAL":
                        return "boolean";
                    case "IDENTIFIER":
                        Token usedToken = getIdentifier(tn);
                        usedToken.setUsed();
                        return usedToken.getData().toLowerCase();
                    default:
                        return "";
                }
            default:
                return "";
        }
    }

    /**
     * This method runs after all the assignment and error checking, It's purpose is to find warnings for unassigned variables
     * and never used variables by checking flags set during table creation. It is also recursive
     * @param stn is the current symbol table node in the tree
     */
    private void checkForWarnings(SymbolTableNode stn)
    {
        for (Map.Entry<Character, Token> entry : stn.getSymbolTable().entrySet())
        {
            Token t = entry.getValue();
            Character c = entry.getKey();
            if(! t.isIdAssigned())
                System.out.println("WARNING: Identifier: " + c + " was declared, but never assigned a value. Line: " + t.getLine() + ", Position: " + t.getPosition());
            if(! t.isIdUsed())
                System.out.println("WARNING: Identifier: " + c + " was declared, but never used. Line: " + t.getLine() + ", Position: " + t.getPosition());
        }

        for (SymbolTableNode child: stn.getChildren())
            checkForWarnings(child);
    }


    /**
     * This method is called by the main method, and ultimately returns the Dot formatted text for image creation.  It
     * calls the recursive method tdotRecursion.  It adds the opening statement and ending statement to the file
     * @return Dot formatted file
     */
    public String toDot()
    {

        String tDot = "graph tree{\nnode [shape=plaintext]\n";
        tDot += tDotRecursion(stt.getRoot()) + "}";
        return tDot;
    }

    /**
     * This is the worker method for making the tree into a tdot file.  Each symbol table is added and connected to the child
     * and the string are recursively returned to build one long string.It uses tdot voodoo combined with html tables to
     * add all the symbol tables and add all the contents of each symbol table.  This dot file will then turn into a beautiful graph picture
     * @param stn is the current table node
     * @return String that is the cu
     */
    public String tDotRecursion(SymbolTableNode stn) {

        String s = "";
        s += new StringBuilder().append("\"").append(stn.getUUID()).append("\" ").append(hashToHTMLTable(stn)).toString();


        if (!stn.isLeaf()) {
            for (SymbolTableNode child : stn.getChildren()) {
                s += new StringBuilder().append("\"").append(stn.getUUID()).append("\" -- \"").append(child.getUUID()).append("\"\n").toString();
            }


            for (SymbolTableNode child : stn.getChildren()) {
                s += tDotRecursion(child);
            }
        }

        return s;
    }

    /**
     * This helper method to the dot file creation takes the node, and gets the hashtable, and then makes a dot representation
     * for each member of the symbol table.
     * @param stn is the symbol table node that is going to be made to dot
     * @return string of the dot sequence for that symbol table node, which contains html table
     */
    public String hashToHTMLTable(SymbolTableNode stn)
    {
        String TableAsString = "[label=<\n<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\">\n";
        TableAsString += new StringBuilder().append("<TR><TD><b>").append("Symbol Table").append("</b></TD></TR>\n");
        for (Map.Entry<Character, Token> entry : stn.getSymbolTable().entrySet())
        {
            String token = entry.getValue().getData();
            String characterKey = entry.getKey().toString();
            TableAsString += new StringBuilder().append("<TR><TD>").append(characterKey).append("</TD><TD>").append(token).append("</TD></TR>\n").toString();
        }
        TableAsString += "</TABLE>>];\n";
        return TableAsString;
    }


}
