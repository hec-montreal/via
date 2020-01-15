package org.via.enums;

public enum eWaitingRoomAccessMode 
{
    Desactive(0),
    Automatique(1),
    Manuel(2);
    
    private final int value;
    
    private eWaitingRoomAccessMode(int val) 
    {
            this.value = val;
    }
    
    
    public static eWaitingRoomAccessMode GetWaitingRoomAccessModeType(int val) 
    {
       switch (val)
       {
          case 0:
              return Desactive;
          case 1:
              return Automatique;
          case 2:
              return Manuel;
         default:
              return Desactive;
      }
    }
       
    
    public int getValue() 
    {
            return this.value;
    }
    
    @Override
    public String toString()
    {
        switch (value)
        {
           case 0:
               return "Desactive";
           case 1:
               return "Automatique";
           case 2:
               return "Manuel";
          default:
               return "Desactive";
        }
    }
    
}
