package org.via.enums;

public enum eAudioType 
{
    VOIP(1),
    Telephone(2),
    TelephoneOnly(3),
    PontExterne(4),
    Mixte(5);
    
    
    private final int value;
    
    private eAudioType(int val) 
    {
            this.value = val;
    }
    
    
    public static eAudioType GetAudioType(int val) 
    {
       switch (val)
       {
          case 1:
              return VOIP;
          case 2:
              return Telephone;
          case 3:
              return TelephoneOnly;
          case 4:
              return PontExterne;
          case 5:
              return Mixte;
         default:
              return VOIP;
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
              return "VOIP";
          case 2:
              return "Telephone";
          case 3:
              return "TelephoneOnly";
          case 4:
              return "PontExterne";
          case 5:
              return "Mixte";
         default:
              return "VOIP";
        }
    }
}
