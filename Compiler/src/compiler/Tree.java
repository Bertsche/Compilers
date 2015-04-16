

package compiler;
import java.util.Objects;

/**
 * This is the class that holds the nodes for the AST and the CST
 */
public class Tree
{
  TreeNode root;
  TreeNode current;
  public Tree()
  {
    this.root = new TreeNode();
    this.current = root;
  }

  /**
   * Add branchNode adds a node with grammar type and sets parent, and adds new node to parents children
   * @param grammarType
   */
  public void addBranchNode(String grammarType)
  {
    TreeNode node = new TreeNode(current, grammarType);
    current.addChild(node);
    current = node;
  }

  /**
   * Adds leaf node and sets its parent, and passes in the token
   * @param myToken
   */
  public void addLeafNode(Token myToken)
  {
    TreeNode node = new TreeNode(current, myToken);
    current.addChild(node);
  }

  /**
   * Jumps the current node to the parent of the current node
   */
  public void jumpToParent()
  {
    current = current.getParent();
  }

  /**
   * This is called to make a dot file for making a nice picture version of the tree.  It calls the recursive helper
   * tdotrecursion with the root of the ast
   * @return String complete dot file
   */
  public String toDot()
  {
	  
	  String tDot = "graph tree{\n";
	  tDot += tDotRecursion(this.root) + "}";
	  
	  return tDot;
  }

  /**
   * This is called by the parser to fix the original created AST.  It calls the repair ast recursive helper with the root of the tree
   */
  public void repairAst()
  {
    this.repairAstWorker(this.root);
  }

  /**
   * This takes on the ast tree node and recursively traverses to find boolean expr and int expr.  They can create children that are singular and
   * do not fit the form, because they resolve to single tokens.  This goes and and finds these instances and calls the
   * treenode class that fixes these nodes
   * @param tn
   */
  private void repairAstWorker(TreeNode tn)
  {
    String gram = tn.getGrammarType();
    if(Objects.equals(gram, "BOOLEAN EXPR") || Objects.equals(gram, "INT EXPR"))
    {
      if(tn.getChildren().size() == 1)
        tn.replaceWithChild();
    }
    if(!tn.getGrammarType().equals("LEAF"))
    {
      for(TreeNode child:tn.getChildren())
        repairAstWorker(child);
    }
  }

  /**
   * This is the Helper for the tdot maker that recursively descends and makes dot file uses strings and dot file creation
   * shenanigans. Annoying to get it rights, but it works and looks awesome
   * @param tn
   * @return
   */
  private String tDotRecursion(TreeNode tn)
  {
	  /*String testOutput = tn.getGrammarType();
	  if (testOutput == "LEAF")
		  testOutput += tn.getToken().getData();
	  
	  
	  System.out.println(testOutput);
	  */
	  String uuID = tn.getUUID();
	String s = "";
	s += "\"" +  uuID + "\" [label=\"";
	if(Objects.equals(tn.getGrammarType(), "LEAF"))
		s+= tn.getToken().getData().replace('"', '\'') +"\"]\n";
	else
		s+= tn.getGrammarType() + "\"]\n";
	
	if (!tn.isLeaf())
	{
		for(TreeNode child : tn.getChildren())
		{
				s += "\"" + uuID + "\" -- \"" + child.getUUID() + "\"\n";
		}
		
		
		
		for(TreeNode child : tn.getChildren())
		{
			s += tDotRecursion(child);
		}	
	}
	
	return s;			  
		  
	  
  }
  public TreeNode getRoot()
  {
	  return root;
  }

}

