package compiler;

import java.util.ArrayList;

/**
 * Disclaimer:  This is not actually a table, but the temp object, poorly named.
 * Works by having a temp name that is the location, and a list of ints that is the location in the codegen array where the variable is accesses by mem location
 * so that when the actual variable is set in this method, those locations can be changed to the actual value.
 * Created by ryan on 5/8/15.
 */
public class TempAddressTable
{
    //variables,
    public Byte tempName;
    private char variableName;
    private String uuid;
    private int offset;
    private ArrayList<Integer> locationsUsed;

    public TempAddressTable(Token t)
    {
        tempName = (byte)254;
        variableName = t.getData().charAt(0);
        uuid = t.getScope();
        this.offset = 0;
        locationsUsed  = new ArrayList<>();
    }
    //Accessors and getter.  Nothing much to see here folks
    public void addUsed(int currentLocation)
    {
        locationsUsed.add(currentLocation);
    }

    public ArrayList<Integer> getUsed()
    {
        return locationsUsed;
    }
    public String getUUID()
    {
        return  uuid;
    }

    public char getVariableName()
    {
        return variableName;
    }

    public int getOffset()
    {
        return offset;
    }

    public void setTempName(Byte location)
    {

    }
}
