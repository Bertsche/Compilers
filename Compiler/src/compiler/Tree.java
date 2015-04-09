

package compiler;
import java.util.UUID;

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
	  
	  String tDot = "graph tree{\n";
	  tDot += tDotRecursion(this.root) + "}";
	  
	  return tDot;
  }
  
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
	if(tn.getGrammarType() == "LEAF")
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

