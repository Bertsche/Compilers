

package compiler;


public class Tree
{
  TreeNode root;
  TreeNode current;
  public Tree()
  {
    this.root = new TreeNode();
    this.current = root;
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
  
  public String toDot()
  {
	  
	  String tDot = "diagraph tree{\n";
	  tDot += tDotRecursion(this.root) + "}";
	  
	  return tDot;
  }
  
  private String tDotRecursion(TreeNode tn)
  {
	String s = "";
	if (!tn.isLeaf())
	{
		for(TreeNode child : tn.getChildren())
		{
			if (child.isLeaf())
				s += tn.getGrammarType() + "--" + child.getToken().getData() + ";\n";
			else
				s+= tn.getGrammarType() + "--" + child.getGrammarType() + ";\n";
		}
		
		
		
		for(TreeNode child : tn.getChildren())
		{
			s += tDotRecursion(child);
		}	
	}
	
	return s;			  
		  
	  
  }

}

