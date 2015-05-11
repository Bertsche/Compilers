package compiler;

import sun.io.CharToByteASCII;
import sun.nio.cs.US_ASCII;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.DatatypeConverter;

/**
 * @author Ryan Bertsche
 */
public class MachineCodeGenerator
{
    Tree ast;
    SymbolTableTree stt;
    List<TempAddressTable> addressTable;
    byte[] outputCode;
    int currentPosition;
    int currentHeapLocation = 255;


    public MachineCodeGenerator(Tree ast, SymbolTableTree stt) throws Exception
    {
        this.ast = ast;
        this.stt = stt;
        addressTable = new ArrayList<>();
        outputCode =new byte[256];
        currentPosition = 0;
        generator(ast.getRoot());
    }



    public void generator(TreeNode astNode) throws Exception
    {
        assignHelper(astNode.getChildren().get(0));
        addByte((byte)0);
        for(TempAddressTable tad:addressTable)
        {
            tad.tempName = (byte)currentPosition;
            incrementPosition();
        }
        for(currentPosition = currentPosition; currentPosition<=currentHeapLocation;currentPosition++)
        {
            outputCode[currentPosition] = (byte)0;
        }
        String output;
        output = DatatypeConverter.printHexBinary(outputCode);
        System.out.print(output);





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
    private void isIf(TreeNode tn) throws Exception
    {
        ArrayList<TreeNode> child = tn.getChildren();
        byte tempJump = 0x01;
        int jumpVariable;
        assignHelper(child.get(0));
        addByte((byte) 0x8D);
        addByte((byte) currentHeapLocation);
        addByte((byte) 0);
        addByte((byte) 0xA2);
        addByte((byte) 1);
        addByte((byte)0xEC);
        addByte((byte) currentHeapLocation);
        addByte((byte)0);
        addByte((byte)0xD0);
        addByte(tempJump);
        jumpVariable = currentPosition;
        assignHelper(child.get(1));
        jumpVariable = currentPosition - jumpVariable;
        tempJump = (byte)jumpVariable;
    }

    private void isPrint(TreeNode tn) throws Exception
    {
        TreeNode child = tn.getChildren().get(0);
        addByte((byte)0xA2);
        if(tn.isLeaf() && tn.getToken().getTokenType().name().equals("IDENTIFIER") && stt.typeForCodegen(tn.getToken().getData().charAt(0), tn.getToken().scope).getData().equals("string"))
        {
            addByte((byte)2);
        }
        else
            addByte((byte)1);
        assignHelper(child);
        addByte((byte)0x8D);
        addByte((byte)currentHeapLocation);
        addByte((byte)0);
        addByte((byte)0xAC);
        addByte((byte) currentHeapLocation);
        addByte((byte)0);
        addByte((byte)0xFF);
    }
    private void isWhile(TreeNode tn) throws Exception
    {
        ArrayList<TreeNode> child = tn.getChildren();
        byte conditionJump = 0x01;
        int cJumpDistance;
        byte jumpBack = 0x01;
        int backJumpDistance;
        backJumpDistance = currentPosition;
        assignHelper(child.get(0));
        addByte((byte) 0x8D);
        addByte((byte) currentHeapLocation);
        addByte((byte) 0);
        addByte((byte) 0xA2);
        addByte((byte) 1);
        addByte((byte) 0xEC);
        addByte((byte) currentHeapLocation);
        addByte((byte) 0);
        addByte((byte) 0xD0);
        addByte(conditionJump);
        cJumpDistance = currentPosition;
        assignHelper(child.get(1));
        addByte((byte) 0xA9);
        addByte((byte) 0);
        addByte((byte) 0x8D);
        addByte((byte) currentHeapLocation);
        addByte((byte) 0);
        addByte((byte) 0xA2);
        addByte((byte) 1);
        addByte((byte) 0xEC);
        addByte((byte) currentHeapLocation);
        addByte((byte) 0);
        addByte((byte) 0xD0);
        addByte(jumpBack);
        backJumpDistance = 255 - (currentPosition - backJumpDistance - 1);
        jumpBack = (byte)backJumpDistance;
        cJumpDistance = currentPosition - cJumpDistance;
        conditionJump = (byte)cJumpDistance;
    }



    private void isDecl(TreeNode tn)
    {
        ArrayList<TreeNode> child = tn.getChildren();
        addressTable.add(new TempAddressTable(child.get(1).getToken()));

    }

    private void isAssignment(TreeNode astNode) throws Exception
    {
        ArrayList<TreeNode> temp = astNode.getChildren();
        assignHelper(temp.get(1));
        TempAddressTable existsCheck = tempExists(temp.get(0).getToken());
       /*This is uneccessary because of semantic analysis
        if(existsCheck == null)
        {
            existsCheck = new TempAddressTable(temp.get(0).getToken());
            addressTable.add(existsCheck);
        }
        */
        addByte((byte) 0x8D);
        addByte(existsCheck.tempName);
        addByte((byte) 0);

    }
    //Need to do boolean expression still
    private void assignHelper(TreeNode tn) throws Exception
    {
        switch(tn.getGrammarType())
        {
            case "BLOCK":
                isBlock(tn);
                break;
            case "PRINT STATEMENT":
                isPrint(tn);
                break;
            case "ASSIGNMENT STATEMENT":
                isAssignment(tn);
                break;
            case "VAR DECL":
                isDecl(tn);
                break;
            case "WHILE STATEMENT":
                isWhile(tn);
                break;
            case "IF STATEMENT":
                isIf(tn);
                break;
            case "INT EXPR":
                isIntExpr(tn);
                break;
            case "BOOLEAN EXPR":
                isBoolExpr(tn);
                break;
            case "LEAF":
                leafChecker(tn);
                break;
        }




    }

    private void isBlock(TreeNode tn) throws Exception
    {
        for(TreeNode child: tn.getChildren())
        {
            assignHelper(child);
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

            addByte((byte) 0x8D);
            addByte((byte) currentHeapLocation);
            addByte((byte) 0);
            addByte((byte) 0xA9);
            addByte((byte) Integer.parseInt(leftChild.getToken().getData()));
            addByte((byte) 0x6D);
            addByte((byte) currentHeapLocation);
            addByte((byte) 0);
        }
        else if(rightChild.getGrammarType().equals("DIGIT"))
        {
            addByte((byte) 0xA9);
            addByte((byte) Integer.parseInt(rightChild.getToken().getData()));
            addByte((byte) 0x8D);
            addByte((byte) currentHeapLocation);
            addByte((byte) 0);
            addByte((byte) 0xA9);
            addByte((byte) Integer.parseInt(leftChild.getToken().getData()));
            addByte((byte) 0x6D);
            addByte((byte) currentHeapLocation);
            addByte((byte) 0);
        }
        else if(rightChild.getGrammarType().equals("IDENTIFIER"))
        {
            addByte((byte) 0xA9);
            addByte((byte) Integer.parseInt(leftChild.getToken().getData()));
            addByte((byte) 0x6D);
            addByte(tempExists(rightChild.getToken()).tempName);
            addByte((byte) 0);
        }
    }

    private void isBoolExpr(TreeNode tn) throws Exception
    {
        ArrayList<TreeNode> child = tn.getChildren();
        boolean equals;
        TreeNode leftChild = child.get(0);
        TreeNode operator = child.get(1);
        TreeNode rightChild = child.get(2);

        assignHelper(leftChild);
        addByte((byte) 0x8D);
        addByte((byte) currentHeapLocation);
        addByte((byte) 0);
        addByte((byte) 0xAE);
        addByte((byte) currentHeapLocation);
        addByte((byte) 0);


        assignHelper(rightChild);
        addByte((byte) 0x8D);
        addByte((byte) currentHeapLocation);
        addByte((byte) 0);
        if(operator.getToken().getData().equals("=="))
        {
            equals = true;
        }
        else
            equals = false;
        addByte((byte) 0xEC);
        addByte((byte) currentHeapLocation);
        addByte((byte)0);
        if(equals)
        {
            addByte((byte)0xA9);
            addByte((byte)0);
            addByte((byte)0xD0);
            addByte((byte)2);
            addByte((byte)0xA9);
            addByte((byte)1);
        }
        else
        {
            addByte((byte)0xA9);
            addByte((byte)1);
            addByte((byte)0xD0);
            addByte((byte)2);
            addByte((byte)0xA9);
            addByte((byte)0);
        }



    }
    private void leafChecker(TreeNode tn) throws Exception
    {
        Token temp = tn.getToken();
        switch(temp.getTokenType().name())
        {
            case "DIGIT":
                addByte((byte)0xA9);
                addByte((byte) Integer.parseInt(temp.getData()));
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
                addByte((byte)0xAD);
                addByte((byte)(currentHeapLocation +1));
                addByte((byte)0);
                break;
            case "BOOLVAL":
                addByte((byte)0xA9);
                byte tempB;
                if(temp.getData().equals("true"))
                    tempB = (byte)1;
                else
                    tempB = (byte)0;
                addByte(tempB);
                break;
            case "IDENTIFIER":
                TempAddressTable existing = tempExists(temp);
                addByte((byte)0xAD);
                addByte(existing.tempName);
                addByte((byte)0);
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

    private void addByte(byte opCode) throws Exception
    {
        outputCode[currentPosition] = opCode;
        incrementPosition();
    }






}
