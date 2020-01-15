package org.via.enums;

public enum eActivityType 
{
    Normale(1),
    Permanente(2);
    
    private final int value;
    
    private eActivityType(int val) 
    {
            this.value = val;
    }
    
    
    public static eActivityType GetActivityType(int val) 
    {
       switch (val)
       {
          case 1:
              return Normale;
          case 2 :
              return Permanente;
          case 3 :
              return Permanente;
          default:
              return Normale;
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
               return "Normale";
           case 2:
               return "Permanente";
          case 3 :
               return "Permanente";
          default:
               return "Normale";
        }
    }
    
}
