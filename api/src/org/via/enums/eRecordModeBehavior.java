package org.via.enums;

public enum eRecordModeBehavior 
{
    Automatique(1),
    Manuel(2);
    
    private final int value;
    
    private eRecordModeBehavior(int val) 
    {
            this.value = val;
    }
    
    
    public static eRecordModeBehavior GetRecordModeBehavior(int val) 
    {
       switch (val)
       {
          case 1:
              return Automatique;
          case 2:
              return Manuel;
         default:
              return Automatique;
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
           case 1:
               return "Automatique";
           case 2:
               return "Manuel";
          default:
               return "Automatique";
        }
    }
    
}
