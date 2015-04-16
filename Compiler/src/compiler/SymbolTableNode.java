package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


/**
 * This does the same functions as treenode, but designed for symbol tables, so similar things won't be commented for the
 * sake of my sanity, and I think you can deduce what they do on your own.  Main difference is that the nodes hold a hashmap
 * of all the symbols as a token of the identifier, hashed by the character that is declared
 * Created by ryan on 4/13/15.
 */
public class SymbolTableNode
{
    private HashMap<Character, Token> symbolTable;
    private SymbolTableNode parent;
    private ArrayList<SymbolTableNode> children;
    private boolean isRoot;
    private String uuID;


    public SymbolTableNode()
    {
        symbolTable  = new HashMap<Character, Token>();
        isRoot = true;
        children = new ArrayList<SymbolTableNode>();
        parent = null;
        uuID = java.util.UUID.randomUUID().toString();
    }

    public SymbolTableNode(SymbolTableNode parent)
    {
        symbolTable = new HashMap<Character, Token>();
        this.parent = parent;
        children = new ArrayList<SymbolTableNode>();
        isRoot = false;
        uuID = java.util.UUID.randomUUID().toString();
    }

    public SymbolTableNode getParent()
    {
        return this.parent;

    }

    public ArrayList<SymbolTableNode> getChildren()
    {
        return this.children;
    }

    public void addChild(SymbolTableNode stn)
    {
        this.children.add(stn);
    }

    public void addAsChildtoParent()
    {
        this.parent.addChild(this);
    }

    /**
     * This is called by the tree class to add a symbol to the symbol table if it doesn;t already exist, if it does already
     * exist, it returns false, so an error can be thrown elsewhere
     * @param c is the character to hash upon
     * @param t is the token, which is the value to be added to the hashmap
     * @return true if added, or false if already exists
     */
    public boolean addSymbol(Character c, Token t) {
        boolean collision = symbolTable.containsKey(c);
        if (collision)
            return false;
        else {
            symbolTable.put(c, t);
            System.out.println(c + "<--- was added to symbol table with token: " + t.getData());
            return true;
        }
    }

    /**
     * Returns the token at a particular character hash.  token is null if nothing exists at hash
     * @param c is the character key
     * @return the token if its there, or null if there is no value at that key
     */
    public Token getValue(Character c)
    {
        return symbolTable.get(c);

    }

    /**
     * This returns the hashmap that is the symbol table
     * @return hashmap symboltable
     */
    public HashMap<Character, Token> getSymbolTable()
    {
        return symbolTable;
    }

    public String getUUID()
    {
        return this.uuID;
    }

    public boolean isLeaf()
    {
        return children.isEmpty();
    }




}
