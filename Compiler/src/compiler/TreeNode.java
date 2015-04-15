

package compiler;

import java.util.*;
import java.util.UUID;


public class TreeNode
{
    private TreeNode parent;
    private ArrayList<TreeNode> children;
    private String grammarType;
    private Token myToken;
    private String uuID;

  public TreeNode()
  {
    this.parent = null;
    this.grammarType = "ROOT";
    this.children = new ArrayList<TreeNode>();
    this.myToken = null;
    this.uuID = java.util.UUID.randomUUID().toString();
  }

  public TreeNode(TreeNode parent, String grammarType)
  {
    this.parent = parent;
    this.grammarType = grammarType;
    this.children = new ArrayList<TreeNode>();
    this.myToken = null;
    this.uuID = java.util.UUID.randomUUID().toString();
  }

  public TreeNode(TreeNode parent, Token myToken)
  {
    this.parent = parent;
    this.grammarType = "LEAF";
    this.myToken = myToken;
    this.children = new ArrayList<TreeNode>();
    this.uuID = java.util.UUID.randomUUID().toString();
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
  public ArrayList<TreeNode> getChildren()
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
  
  public String getUUID()
  {
	  return this.uuID;
  }

  public void orphanTNode(){this.parent = null;}
  public void replaceWithChild()
  {
    TreeNode child = this.children.get(0);
    this.grammarType = "LEAF";
    this.children.clear();
    this.myToken = child.getToken();
    child.orphanTNode();

  }
		  



}
