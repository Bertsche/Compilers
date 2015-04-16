

package compiler;

import java.util.*;
import java.util.UUID;

/**
 * This is a tree node for the ast and cst nodes that are created by the parser.
 * UUid is used specifically to identify each node for the dot file creation, so connections are unique. The odds of
 * duplicate UUid are beyond negligible.
 * @author Ryan Bertsche
 *
 */
public class TreeNode
{
    private TreeNode parent;
    private ArrayList<TreeNode> children;
    private String grammarType;
    private Token myToken;
    private String uuID;

  /**
   * This is the constructor for the treeNode that is the root.  All the parameters are instantiated to defaults, except type is
   * grammar type is root, because it is a empty root node
   */
  public TreeNode()
  {
    this.parent = null;
    this.grammarType = "ROOT";
    this.children = new ArrayList<TreeNode>();
    this.myToken = null;
    this.uuID = java.util.UUID.randomUUID().toString();
  }

  /**
   * This is for adding non leaf nodes
   * @param parent is the parent to the new node being created
   * @param grammarType is the name of the grammar type used when analyzing tree later
   */
  public TreeNode(TreeNode parent, String grammarType)
  {
    this.parent = parent;
    this.grammarType = grammarType;
    this.children = new ArrayList<TreeNode>();
    this.myToken = null;
    this.uuID = java.util.UUID.randomUUID().toString();
  }

  /**
   * This is for instantiating a leaf node, and setting the token it holds, with grammar type of LEAF
   * @param parent
   * @param myToken
   */
  public TreeNode(TreeNode parent, Token myToken)
  {
    this.parent = parent;
    this.grammarType = "LEAF";
    this.myToken = myToken;
    this.children = new ArrayList<TreeNode>();
    this.uuID = java.util.UUID.randomUUID().toString();
  }

  /**
   * setter for adding child to node
   * @param child
   */
  public void addChild(TreeNode child)
  {
    this.children.add(child);
  }

  /**
   * getter for getting parent of a node
   * @return TreeNode parent
   */
  public TreeNode getParent()
  {
    return this.parent;
  }

  /**
   * getter for grammar type
   * @return String Grammar Type
   */
  public String getGrammarType()
  {
	  return this.grammarType;
  }

  /**
   * Getter that returns list of all children
   * @return ArrayList of TreeNodes that is all the children
   */
  public ArrayList<TreeNode> getChildren()
  {
	  return this.children;
  }

  /**
   * Returns true if node is a leaf, based on the number of children, because some non-leaf grammar types can epsilon escape and be leafs
   * @return true if there are no children
   */
  public boolean isLeaf()
  {
	  return this.children.isEmpty();
  }

  /**
   * Getter for the tokens
   * @return
   */
  public Token getToken()
  {
	  return this.myToken;
  }

  /**
   * gets unique identifier for node
   * @return
   */
  public String getUUID()
  {
	  return this.uuID;
  }

  /**
   * when deleting a node, setting the parent to null helps orphan it
   */
  public void orphanTNode(){this.parent = null;}

  /**
   * This is called to replace one node with its child, used for special ast fixing, because certain expressions can
   * resolve a single leaf, or an expression, so single children ones are replaced by children
   */
  public void replaceWithChild()
  {
    TreeNode child = this.children.get(0);
    this.grammarType = "LEAF";
    this.children.clear();
    this.myToken = child.getToken();
    child.orphanTNode();

  }
		  



}
