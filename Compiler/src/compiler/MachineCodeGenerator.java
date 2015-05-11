package compiler;

import sun.io.CharToByteASCII;
import sun.nio.cs.US_ASCII;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ryan Bertsche
 */
public class MachineCodeGenerator
{
    Tree ast;
    List<TempAddressTable> addressTable;
    Byte[] outputCode;
    int currentPosition;
    int currentHeapLocation = 255;


    public MachineCodeGenerator(Tree ast)
    {
        this.ast = ast;
        addressTable = new ArrayList<>();
        outputCode =new Byte[256];
        currentPosition = 0;
    }



    public void generator(TreeNode astNode) {
       /* String gT = astNode.getGrammarType();
        switch (gT) {
            case "BLOCK":
                break;
            case "PRINT STATEMENT":
                asTree.addBranchNode(gT);
                jump = true;
                break;
            case "VAR DECL":
                asTree.addBranchNode(gT);
                jump = true;
                break;
            case "IF STATEMENT":
                asTree.addBranchNode(gT);
                jump = true;
                break;
            case "WHILE STATEMENT":
                asTree.addBranchNode(gT);
                jump = true;
                break;
            case "ASSIGNMENT STATEMENT":
                asTree.addBranchNode(gT);
                jump = true;
                break;
            case "INT EXPR":
                asTree.addBranchNode(gT);
                jump = true;
                break;
            case "BOOLEAN EXPR":
                asTree.addBranchNode(gT);
                jump = true;
                break;
            //Leaf nodes are ones that hold tokens so they are added with a different constructor
            case "LEAF":
                Token tempToken = astNode.getToken();
                switch (tempToken.getTokenType().name()) {
                    case "DIGIT":
                        asTree.addLeafNode(tempToken);
                        break;
                    case "TYPE":
                        asTree.addLeafNode(tempToken);
                        break;
                    case "STRINGLITERAL":
                        asTree.addLeafNode(tempToken);
                        break;
                    case "BOOLOP":
                        asTree.addLeafNode(tempToken);
                        break;
                    case "BOOLVAL":
                        asTree.addLeafNode(tempToken);
                        break;
                    case "IDENTIFIER":
                        asTree.addLeafNode(tempToken);
                    default:
                        break;
                }
            default:
                break;
        }*/
    }

    private void isAssignment(TreeNode astNode) throws Exception
    {
        ArrayList<TreeNode> temp = astNode.getChildren();
        assignHelper(temp.get(1));
        TempAddressTable existsCheck = tempExists(temp.get(0).getToken());
        if(existsCheck == null)
        {

            existsCheck = new TempAddressTable(temp.get(0).getToken());
            addressTable.add(existsCheck);
        }
        outputCode[currentPosition] = new Byte((byte)0x8D);
        incrementPosition();
        outputCode[currentPosition] = existsCheck.tempName;
        incrementPosition();
        outputCode[currentPosition] = new Byte((byte)0);
        incrementPosition();

    }
    //Need to do boolean expression still
    private void assignHelper(TreeNode tn) throws Exception
    {
        switch(tn.getGrammarType())
        {
            case "INT EXPR":
                isIntExpr(tn);
            case "BOOLEAN EXPR":
                isBoolExpr(tn);

            case "LEAF":
                Token temp = tn.getToken();
                switch(temp.getTokenType().name()){
                    case "DIGIT":
                        outputCode[currentPosition] = new Byte((byte)0xA9);
                        incrementPosition();
                        outputCode[currentPosition] = new Byte((byte) Integer.parseInt(temp.getData()));
                        incrementPosition();
                        break;
                    case "STRINGLITERAL":
                        String s = temp.getData();
                        int start = currentHeapLocation - s.length();
                        changeHeapLocation(currentHeapLocation - s.length() - 1);
                        for(byte b: tn.getToken().getData().getBytes(StandardCharsets.US_ASCII))
                        {
                            outputCode[start] = new Byte(b);
                            start++;
                        }
                        outputCode[start] = new Byte((byte)0x00);
                        outputCode[currentPosition] = new Byte((byte)0xAD);
                        incrementPosition();
                        outputCode[currentPosition] = new Byte((byte)(currentHeapLocation +1));
                        incrementPosition();
                        outputCode[currentPosition] = new Byte((byte)0);
                        incrementPosition();
                        break;
                    case "BOOLVAL":
                        outputCode[currentPosition] = new Byte((byte)0xA9);
                        incrementPosition();
                        byte tempB;
                        if(temp.getData().equals("true"))
                            tempB = (byte)1;
                        else
                            tempB = (byte)0;
                        outputCode[currentPosition] = new Byte(tempB);
                        incrementPosition();
                        break;
                    case "IDENTIFIER":
                        TempAddressTable existing = tempExists(temp);
                        outputCode[currentPosition] = new Byte((byte)0xAD);
                        incrementPosition();
                        outputCode[currentPosition] = existing.tempName;
                        incrementPosition();
                        outputCode[currentPosition] = new Byte((byte)0);
                        incrementPosition();
                }
                break;
        }


    }

    private void isIntExpr(TreeNode tn) throws Exception
    {
        ArrayList<TreeNode> children = tn.getChildren();
        TreeNode leftChild = children.get(0);
        TreeNode rightChild = children.get(1);
        if(! rightChild.isLeaf())
        {
            isIntExpr(rightChild);
            outputCode[currentPosition] = new Byte((byte)0x8D);
            incrementPosition();
            outputCode[currentPosition] = new Byte((byte)currentHeapLocation);
            incrementPosition();
            outputCode[currentPosition] = new Byte((byte)0);
            incrementPosition();
            outputCode[currentPosition] = new Byte((byte)0xA9);
            incrementPosition();
            outputCode[currentPosition] = new Byte((byte) Integer.parseInt(leftChild.getToken().getData()));
            incrementPosition();
            outputCode[currentPosition] = new Byte((byte) 0x6D);
            incrementPosition();
            outputCode[currentPosition] = new Byte((byte)currentHeapLocation);
            incrementPosition();
            outputCode[currentPosition] = new Byte((byte)0);
            incrementPosition();
        }
        else if(rightChild.getGrammarType().equals("DIGIT"))
        {
            outputCode[currentPosition] = new Byte((byte)0xA9);
            incrementPosition();
            outputCode[currentPosition] = new Byte((byte) Integer.parseInt(rightChild.getToken().getData()));
            incrementPosition();
            outputCode[currentPosition] = new Byte((byte)0x8D);
            incrementPosition();
            outputCode[currentPosition] = new Byte((byte)currentHeapLocation);
            incrementPosition();
            outputCode[currentPosition] = new Byte((byte)0);
            incrementPosition();
            outputCode[currentPosition] = new Byte((byte)0xA9);
            incrementPosition();
            outputCode[currentPosition] = new Byte((byte) Integer.parseInt(leftChild.getToken().getData()));
            incrementPosition();
            outputCode[currentPosition] = new Byte((byte) 0x6D);
            incrementPosition();
            outputCode[currentPosition] = new Byte((byte)currentHeapLocation);
            incrementPosition();
            outputCode[currentPosition] = new Byte((byte)0);
            incrementPosition();
        }
        else if(rightChild.getGrammarType().equals("IDENTIFIER"))
        {
            outputCode[currentPosition] = new Byte((byte)0xA9);
            incrementPosition();
            outputCode[currentPosition] = new Byte((byte) Integer.parseInt(leftChild.getToken().getData()));
            incrementPosition();
            outputCode[currentPosition] = new Byte((byte) 0x6D);
            incrementPosition();
            outputCode[currentPosition] = tempExists(rightChild.getToken()).tempName;
            incrementPosition();
            outputCode[currentPosition] = new Byte((byte)0);
            incrementPosition();
        }
    }

    private void isBoolExpr(TreeNode tn)
    {
        ArrayList<TreeNode> child = tn.getChildren();
        if(child.get(0).isLeaf())
        {

        }
    }
    private void leafChecker(TreeNode tn)
    {
        switch(tn.getToken().getTokenType().name())
        {
            case ""
        }
    }


    private void incrementPosition() throws Exception
    {
        if(currentPosition  == currentHeapLocation)
        {
            throw new Exception("Error,the heap has collided with the program memory.  Program is too large");
        }
        else if(currentPosition == 255)
        {
            throw new Exception("Error, the program ran out of memory.  Program is too large");
        }
        else
            currentPosition++;

    }

    private void changeHeapLocation(int newPosition) throws Exception
    {
        if(currentPosition >= newPosition)
            throw new Exception("Error,the heap has collidid with the program memory.  Program is too large.");
        else
            currentHeapLocation = newPosition;

    }



    private TempAddressTable tempExists(Token t)
    {
        for(TempAddressTable tat: addressTable)
        {
            if((tat.getVariableName() == t.getData().charAt(0)) && (tat.getUUID().equals(t.getScope())))
            {
                return tat;
            }

        }
        return null;
    }






}
