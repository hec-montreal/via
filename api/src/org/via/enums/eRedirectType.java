package org.via.enums;

public enum eRedirectType 
{
    PageDefault(1),
    PageActivity(2),
    PageVia(3),
    PageSupport(4),
    PageSetupWizard(5),
    PageSupport_mn(6),
    PageSetupWizard_nm(7),
    PageResetPasswordNoExp(8); 
    
    private final int value;
    
    private eRedirectType(int val) {
            this.value = val;
    }
    
    public static eRedirectType GeRedirectType(int val) {
       switch (val){
          case 1:
              return PageDefault;
          case 2:
              return PageActivity;
          case 3:
              return PageVia;
          case 4:
              return PageSupport;
          case 5:
              return PageSetupWizard;
          case 6:
              return PageSupport_mn;
          case 7:
              return PageSetupWizard_nm;
          case 8:
              return PageResetPasswordNoExp;
         default:
              return PageDefault;
          }
       }
       
    
    public int getValue() {
            return this.value;
    }
    
    @Override
    public String toString(){
        switch (value){
           case 1:
               return "PageDefault";
           case 2:
               return "PageActivity";
           case 3:
               return "PageVia";
           case 4:
               return "PageSupport";
           case 5:
               return "PageSetupWizard";
           case 6:
               return "PageSupport_mn";
           case 7:
               return "PageSetupWizard_nm";
           case 8:
               return "PageResetPasswordNoExp";
          default:
               return "PageDefault";
           }
    }
}

