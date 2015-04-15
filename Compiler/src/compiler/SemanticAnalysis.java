package compiler;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by ryan on 4/13/15.
 */
public class SemanticAnalysis
{
    Tree ast;
    SymbolTableTree stt;
    int scope = 0;

    public SemanticAnalysis(Tree ast) throws Exception {
        this.ast = ast;
        stt = new SymbolTableTree();
        this.buildSymbolTree(ast.getRoot());
        this.checkForWarnings(stt.getRoot());
    }

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

    private Token getIdentifier(TreeNode tr) throws Exception {
        Token t = stt.findIdentifier(tr.getToken().getData().charAt(0), stt.getCurrent());
        if (t == null)
        {
            Token errorAtToken = tr.getToken();
            throw new Exception("Error : There is an undeclared identifier: " + errorAtToken.getData() + ", at Line: " + errorAtToken.getLine() + ", Position: " + errorAtToken.getPosition());
        }
        return t;
    }


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
                    throw new Exception("There is a type mismatch at Line: " + cList.get(2).getToken().getLine() + ", Position: " + cList.get(2).getToken().getPosition() + ". Expected type: " + c1 + ". Received type: " + c2);

            case "INT EXPR":
                c1 = typeOf(cList.get(0));
                c2 = typeOf(cList.get(1));
                if(c1.equals(c2))
                    return "int";
                else
                   throw new Exception("There is a type mismatch at Line: " + cList.get(2).getToken().getLine() + ", Position: " + cList.get(2).getToken().getPosition() + ". Expected type: " + c1 + ". Received type: " + c2);

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




}
