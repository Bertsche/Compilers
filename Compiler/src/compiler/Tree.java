

package complier;


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
}
