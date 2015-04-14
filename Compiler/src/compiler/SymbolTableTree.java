package compiler;

/**
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

    public void addSymbol(Character c, Token t) throws Exception {
        if(! current.addSymbol(c, t))
        {
            throw new Exception(new StringBuilder().append("You declared the variable ").append(c.toString()).append("twice in the same scope at Line: ").append(t.getLine()).append(", Position: ").append(t.getLine()).toString());
        }
    }

    public void addBranchNode(String grammarType)
    {
        TreeNode node = new TreeNode(current, grammarType);
        current.addChild(node);
        current = node;
    }

    public void addLeafNode(Token myToken)
    {
        TreeNode node = new TreeNode(current, myToken);
        current.addChild(node);
    }

    public void jumpToParent()
    {
        current = current.getParent();
    }
}
