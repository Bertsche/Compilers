package compiler;

import java.util.Objects;

/**
 * Once again very similar to the treenode tree, so only comments on what is different
 * Created by ryan on 4/13/15.
 */
public class SymbolTableTree
{
    SymbolTableNode root;
    SymbolTableNode current;


    public SymbolTableTree()
    {
        this.root = null;
        this.current = null;
    }


    /**
     * Contains check to see if this is the first symbol table, because I didnt want an empty root node with symbol tables
     */
    public void addNode()
    {
        if(this.root == null)
        {
            SymbolTableNode node = new SymbolTableNode();
            this.current = node;
            this.root = node;
        }
        else
        {
            SymbolTableNode node = new SymbolTableNode(current);
            node.addAsChildtoParent();
            this.current = node;
        }


    }

    /**
     * This calls the addSymbol in the symbol table node class, and if that reurns false, it throws a declared twice error
     * @param c character key
     * @param t token value
     * @throws Exception
     */
    public void addSymbol(Character c, Token t) throws Exception {
        if(! current.addSymbol(c, t))
        {
            throw new Exception(new StringBuilder().append("You declared the variable ").append(c.toString()).append("twice in the same scope at Line: ").append(t.getLine()).append(", Position: ").append(t.getLine()).toString());
        }
    }

    /**
     * This class has the all important duty of finding a value in a parse table based on the key.  If its not in the
     * current symbol table, it will go up to the parent table, until it reaches the root.  In that case, if the identifier is not found
     * it returns a null token, which tells the calling function that the variable was never declared
     * @param c
     * @param stn
     * @return
     */
    public Token findIdentifier(Character c, SymbolTableNode stn)
    {
        Token t = stn.getValue(c);
        SymbolTableNode parent = stn.getParent();
        if(t == null && parent != null)
        {
            Token tr = findIdentifier(c, parent);
            return tr;
        }
        else
            return t;
    }

    public SymbolTableNode getCurrent()
    {
        return current;
    }

    public boolean isRoot()
    {
        return root == current;
    }


    public void jumpToParent()
    {
        current = current.getParent();
    }

    public SymbolTableNode getRoot()
    {
        return this.root;
    }





}
