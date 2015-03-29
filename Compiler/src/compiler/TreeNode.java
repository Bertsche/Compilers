

package compiler;

import java.util.*;


public class TreeNode
{
  private TreeNode parent;
  private List<TreeNode> children;
  private String grammarType;
  private Token token;

public TreeNode(TreeNode parent, String grammarType)
{
  this.parent = parent;
  this.grammarType = grammarType;
  this.children = new List<children>;
  this.token = null;
}

public TreeNode(treeNode parent, Token token)
{
  this.parent = parent;
  this.grammarType = "leaf";
  this.token = token;
  this.children = null;
}





}
