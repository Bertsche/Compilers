package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


/**
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

    public Token getValue(Character c)
    {
        return symbolTable.get(c);

    }

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
