package compiler;

import sun.io.CharToByteASCII;
import sun.nio.cs.US_ASCII;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.lang.ArrayUtils;

/**
 * @author Ryan Bertsche
 * This is the class that takes the Abstract syntax tree and the symbol table and generates the machine code
 */
public class MachineCodeGenerator
{
    //pretty self explainable variables
    Tree ast;
    SymbolTableTree stt;
    List<TempAddressTable> addressTable;
    Byte[] outputCode;
    int currentPosition;
    //current heap location is only 254, because 25 is used as a temp memory location
    int currentHeapLocation = 254;
    String output;

    /**
     * Constructor that starts the execution of the program
     * @param ast is the Abstract syntax tree checked and generated earlier in the parser
     * @param stt is the symbol table tree that is passed to help with the address table
     * @throws Exception
     */
    public MachineCodeGenerator(Tree ast, SymbolTableTree stt) throws Exception
    {
        this.ast = ast;
        this.stt = stt;
        output = "";
        addressTable = new ArrayList<>();
        outputCode =new Byte[256];
        currentPosition = 0;
        //starts the process of tanslating to machine code
        generator(ast.getRoot());

    }


    /**
     * This method is the method that calls all the helper method and gets everything going.  After all that is done, the
     * temp variable values are set, and then the output code is backtracked to replace temps with actual locations. Then,
     * the resulting Byte array is translated into a byte array, and then translated into a string of bytecodes, mostly thanks to
     * some really handy java methods
     * @param astNode
     * @throws Exception
     */
    public void generator(TreeNode astNode) throws Exception
    {
        //this is really the main calling function that started out as a simple assignHelper, but evolved into much more.  The name is nostalgic
        assignHelper(astNode.getChildren().get(0));
        //add the terminating symbol to the end of code
        addByte((byte)0);
        //loop through all the temp addresses in the table set the actual address
        for(TempAddressTable tad:addressTable)
        {
            tad.tempName = (byte)currentPosition;
            //System.out.println("Variable " + tad.getVariableName() + ": location is set to " + tad.tempName.toString());
            //for all places that varible is used, reassign that byte to the correct mem loc
            for(Integer i: tad.getUsed())
            {
                outputCode[i] = tad.tempName;
            }
            incrementPosition();
        }
        //fill in the emplty space in array with zeroes
        for(currentPosition = currentPosition; currentPosition<=currentHeapLocation;currentPosition++)
        {
            outputCode[currentPosition] = (byte)0;
        }
        //Convert the Byte array to byte array, with a handy built in, so it can be used by the next built in
        byte primitiveOutput[] = ArrayUtils.toPrimitive(outputCode, (byte)0);
        //convert the byte array to a nice string of hex values.  this is why I like java
        output= DatatypeConverter.printHexBinary(primitiveOutput);
        //System.out.print(output);


    }

    /**
     * This is the poorer named function that is called whenever a new AST node is being read to determine what it is, and wat code to generate for it.
     * This just calls a bunch of other methods that do all the actual work
     * @param tn is the treenode being evaluated
     * @throws Exception
     */
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
                System.out.println("Int Expr Called");
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

    /**
     * Same idea as assignHelper, but is broken apart for cleanliness, and because sometimes I know its a leaf, and just need to call this method.
     * This method actually generates the opcodes in order to load that leaf into the accumulator, which is the register where
     * nodes that would return values put values, and where they are looked for
     * @param tn treenode being evaluated
     * @throws Exception
     */
    private void leafChecker(TreeNode tn) throws Exception
    {
        Token temp = tn.getToken();
        switch(temp.getTokenType().name())
        {
            case "DIGIT":
                addByte((byte) 0xA9);
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
                outputCode[start] = new Byte((byte)0);
                addByte((byte)0xA9);
                addByte((byte) (currentHeapLocation + 1));
                break;
            case "BOOLVAL":
                addByte((byte) 0xA9);
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
                existing.addUsed(currentPosition);
                addByte(existing.tempName);
                addByte((byte) 0);
        }
    }

    /**
     * This is the method that evaluates the if node, and parses down from there, It makes the opcodes, calls for the boolean expression to be evaluated
     * and calls the block to be written and based on what that returns evaluates, the jump will happen or not
     * @param tn treenode being evaluated
     * @throws Exception
     */
    private void isIf(TreeNode tn) throws Exception
    {
        ArrayList<TreeNode> child = tn.getChildren();
        byte tempJump = 0x01;
        int jumpVariable;
        assignHelper(child.get(0));
        addByte((byte) 0x8D);
        addByte((byte) 255);
        addByte((byte) 0);
        addByte((byte) 0xA2);
        addByte((byte) 1);
        addByte((byte)0xEC);
        addByte((byte) 255);
        addByte((byte) 0);
        addByte((byte)0xD0);
        int backLocation = currentPosition;
        addByte(tempJump);
        jumpVariable = currentPosition;
        assignHelper(child.get(1));
        jumpVariable = currentPosition - jumpVariable;
        tempJump = (byte)jumpVariable;
        outputCode[backLocation] = tempJump;
    }

    /**
     * This method prints the the value in the child of the print ast.  It has to check to see id its a string first
     * with a bit of a ass-backwards symboltable lookup, and if it is, set the x register with 2
     * @param tn treenode of print
     * @throws Exception
     */
    private void isPrint(TreeNode tn) throws Exception
    {
        TreeNode child = tn.getChildren().get(0);
        addByte((byte)0xA2);
        //stupid long boolean if that essentially checks if it is a string idetifier or a literal, and set to 2 if it is or 1 if isn't
        if((child.isLeaf()) &&(child.getToken().getTokenType().name().equals("STRINGLITERAL") ||((child.getToken().getTokenType().name().equals("IDENTIFIER")) && (stt.typeForCodegen(child.getToken().getData().charAt(0), child.getToken().scope).getData().equals("string")))))
        {
            addByte((byte)2);
        }
        else
            addByte((byte)1);
        assignHelper(child);
        addByte((byte)0x8D);
        addByte((byte)255);
        addByte((byte)0);
        addByte((byte) 0xAC);
        addByte((byte) 255);
        addByte((byte)0);
        addByte((byte)0xFF);
    }

    /**
     * This method handles the opcode nonsense of doing while loops.The while process is convoluted, and i don't recommend you even look
     * at it, or even try to understand, because even after writing it, I have no love for this nonsense.  Just know it checks, jumps is ut evaluates false
     * colmpletes the block if it doesn't then jumps back to the check and starts it all again.  It borrows pieces from the isIf method, but
     * was just different enough I had to rethink and rewrite the logic for this specific hell-child
     * @param tn
     * @throws Exception
     */
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
        addByte((byte) 255);
        addByte((byte) 0);
        addByte((byte) 0xA2);
        addByte((byte) 1);
        addByte((byte) 0xEC);
        addByte((byte) 255);
        addByte((byte) 0);
        addByte((byte) 0xD0);
        int locConJumpByte = currentPosition;
        addByte(conditionJump);
        cJumpDistance = currentPosition;
        assignHelper(child.get(1));
        addByte((byte) 0xA9);
        addByte((byte) 0);
        addByte((byte) 0x8D);
        addByte((byte) 255);
        addByte((byte) 0);
        addByte((byte) 0xA2);
        addByte((byte) 1);
        addByte((byte) 0xEC);
        addByte((byte) 255);
        addByte((byte) 0);
        addByte((byte) 0xD0);
        int locJumpBackByte = currentPosition;
        addByte(jumpBack);
        backJumpDistance = 255 - (currentPosition - backJumpDistance - 1);
        jumpBack = (byte)backJumpDistance;
        outputCode[locJumpBackByte] = jumpBack;
        cJumpDistance = currentPosition - cJumpDistance;
        conditionJump = (byte)cJumpDistance;
        outputCode[locConJumpByte] = conditionJump;
    }


    /**
     * This was my favorite method to write.  Simply adds the symbol to the temp table.
     * @param tn the var decl ast node
     */
    private void isDecl(TreeNode tn)
    {
        ArrayList<TreeNode> child = tn.getChildren();
        addressTable.add(new TempAddressTable(child.get(1).getToken()));

    }

    /**
     * This, as you can probably figure out if you are still reading all the comments, is the method that handles the assignment of variables.
     *
     * @param astNode the assignment ast, oddly i didn;t call it tn
     * @throws Exception
     */
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
        existsCheck.addUsed(currentPosition);
        addByte(existsCheck.tempName);
        addByte((byte) 0);

    }


    /**
     * If its a block, evaluate all the chil'en
     * @param tn vlock ast node
     * @throws Exception
     */
    private void isBlock(TreeNode tn) throws Exception
    {
        for(TreeNode child: tn.getChildren())
        {
            assignHelper(child);
        }
    }

    /**
     * This evaluates and int expression.  This method took a bit of finnesse, because of the possibility of all different
     * types being in the right child, like continuous addition.  It works, trust me.
     * @param tn int expr ast node
     * @throws Exception
     */
    private void isIntExpr(TreeNode tn) throws Exception
    {
        ArrayList<TreeNode> children = tn.getChildren();
        TreeNode leftChild = children.get(0);
        TreeNode rightChild = children.get(1);
        if(! rightChild.isLeaf())
        {
            isIntExpr(rightChild);

            addByte((byte) 0x8D);
            addByte((byte) 255);
            addByte((byte) 0);
            addByte((byte) 0xA9);
            addByte((byte) Integer.parseInt(leftChild.getToken().getData()));
            addByte((byte) 0x6D);
            addByte((byte) 255);
            addByte((byte) 0);
        }
        else if(rightChild.getGrammarType().equals("LEAF") && rightChild.getToken().getTokenType().name().equals("DIGIT"))
        {
            addByte((byte) 0xA9);
            addByte((byte) Integer.parseInt(rightChild.getToken().getData()));
            addByte((byte) 0x8D);
            addByte((byte) 255);
            addByte((byte) 0);
            addByte((byte) 0xA9);
            addByte((byte) Integer.parseInt(leftChild.getToken().getData()));
            addByte((byte) 0x6D);
            addByte((byte) 255);
            addByte((byte) 0);
        }
        else if(rightChild.getGrammarType().equals("LEAF") && rightChild.getToken().getTokenType().name().equals("IDENTIFIER"))
        {
            addByte((byte) 0xA9);
            addByte((byte) Integer.parseInt(leftChild.getToken().getData()));
            addByte((byte) 0x6D);
            TempAddressTable tExists = tempExists(rightChild.getToken());
            tExists.addUsed(currentPosition);
            addByte(tExists.tempName);
            addByte((byte) 0);
        }
    }

    /**
     * Another annoying op code generator, this time for boolean exprs. As far as I can remember(I'm pretty sure I blacked out
     * when writing this method) it evaluates all the different possibilities, and writes either a 1 or 0 to the accumulator
     * based on how it evaluates, and whether it is a == or !=.  this is because it can be used fot assignment and evaluation
     * @param tn boolean expr
     * @throws Exception
     */
    private void isBoolExpr(TreeNode tn) throws Exception
    {
        ArrayList<TreeNode> child = tn.getChildren();
        boolean equals;
        TreeNode leftChild = child.get(0);
        TreeNode operator = child.get(1);
        TreeNode rightChild = child.get(2);

        assignHelper(leftChild);
        addByte((byte) 0x8D);
        addByte((byte) 255);
        addByte((byte) 0);
        addByte((byte) 0xAE);
        addByte((byte) 255);
        addByte((byte) 0);


        assignHelper(rightChild);
        addByte((byte) 0x8D);
        addByte((byte) 255);
        addByte((byte) 0);
        if(operator.getToken().getData().equals("=="))
        {
            equals = true;
        }
        else
            equals = false;
        addByte((byte) 0xEC);
        addByte((byte) 255);
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


    /**
     * Increments the current position, after checking that there is no heap, memory collision
     * @throws Exception
     */
    private void incrementPosition() throws Exception
    {
        if(currentPosition  >= currentHeapLocation)
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

    /**
     * seldom used method to change the heap location, when building strings, just to check the heap collision
     * @param newPosition
     * @throws Exception
     */
    private void changeHeapLocation(int newPosition) throws Exception
    {
        if(currentPosition >= newPosition)
            throw new Exception("Error,the heap has collidid with the program memory.  Program is too large.");
        else
            currentHeapLocation = newPosition;
        System.out.println("Heap Location changed to: " + newPosition);

    }


    /**
     * This helper returns the addresstable object for the matching id token, for addressing purposes.  Null if not in table yet, which can be evaluated by calling function
     * @param t token to be matched
     * @return TempAddressTab;e object
     */
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

    /**
     * Little helper for adding a byte that saves me from having to repeat the same thing over and over
     * @param opCode
     * @throws Exception
     */
    private void addByte(Byte opCode) throws Exception
    {
        outputCode[currentPosition] = opCode;
        incrementPosition();
    }

    /**
     * getter for output code hex string, so it can be printed
     * @return
     */
    public String getOutput()
    {
        return output;
    }





}
