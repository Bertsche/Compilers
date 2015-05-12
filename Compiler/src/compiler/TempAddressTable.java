package compiler;

import java.util.ArrayList;

/**
 * Created by ryan on 5/8/15.
 */
public class TempAddressTable
{
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
