

package compiler;

import java.util.*;


public class TreeNode
{
    private TreeNode parent;
    private List<TreeNode> children;
    private String grammarType;
    private Token myToken;

  public TreeNode()
  {
    this.parent = null;
    this.grammarType = "ROOT";
    this.children = new ArrayList<TreeNode>();
    this.myToken = null;
  }

  public TreeNode(TreeNode parent, String grammarType)
  {
    this.parent = parent;
    this.grammarType = grammarType;
    this.children = new ArrayList<TreeNode>();
    this.myToken = null;
  }

  public TreeNode(TreeNode parent, Token myToken)
  {
    this.parent = parent;
    this.grammarType = "LEAF";
    this.myToken = myToken;
    this.children = null;
  }

  public void addChild(TreeNode child)
  {
    this.children.add(child);
  }

  public TreeNode getParent()
  {
    return this.parent;
  }

  public String getGrammarType()
  {
	  return this.grammarType;
  }
  public List<TreeNode> getChildren()
  {
	  return this.children;
  }
  public boolean isLeaf()
  {
	  return this.children.isEmpty();
  }
  
  public Token getToken()
  {
	  return this.myToken;
  }




}
