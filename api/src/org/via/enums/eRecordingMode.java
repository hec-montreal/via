package org.via.enums;

public enum eRecordingMode 
{
    Desactive(0),
    Unifie(1),
    Multiple(2);
    
    private final int value;
    
    private eRecordingMode(int val) 
    {
            this.value = val;
    }
    
    
    public static eRecordingMode GetRecordingMode(int val) 
    {
       switch (val)
       {
          case 0:
              return Desactive;
          case 1:
              return Unifie;
          case 2:
              return Multiple;
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
               return "Unifie";
           case 2:
               return "Multiple";
          default:
               return "Desactive";
        }
    }
    
}
