package compiler;

/**
 * Created by ryan on 5/8/15.
 */
public class TempAddressTable
{
    public Byte tempName;
    private char variableName;
    private String uuid;
    private int offset;

    public TempAddressTable(Token t)
    {
        tempName = 0x1;
        variableName = t.getData().charAt(0);
        uuid = t.getScope();
        this.offset = 0;
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
