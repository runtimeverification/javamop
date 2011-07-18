package logicrepository.plugins.ltl;

public enum LTLType {
     // ordering is important to compare LTLFormula
     //we want true false and atoms to be sorted to the
     //leftmost in AND, OR, XOR nodes for optimization
     //purposes (inlining pass)
     T,   //True
     F,   //False
     A,   //atom
     NEG, //negation
     AND, //and
     XOR, //xor
     IMP, //implication
     IFF, //double implication
     OR,  //or
     U,   //until
     DU,  //dual of until
     S,   //since
     DS,  //dual of since
     X,   //next
     DX,  //dual of next
     Y,   //previously (yesterday)
     DY,  //dual of previously
     END; //END

     public String toString(){
       switch(this) {
         case T:    return "t";
         case F:    return "f";
         case A:    return "atom";
         case NEG : return "not ";
         case AND : return " and ";
         case XOR : return " xor ";
         case IMP : return " => ";
         case IFF : return " <=> ";
         case OR:   return " or ";
         case U:    return " U ";
         case DU:   return " ~U ";
         case S:    return " S ";
         case DS:   return " ~S ";
         case X:    return "o ";
         case DX:   return "~o ";
         case Y:    return "(*) ";
         case DY:   return "~(*) ";
         case END:   return "END";
         default:   return "error";
       }
     }

      public int toInt(){
       switch(this) {
         case T:    return 1;
         case F:    return 10;
         case A:    return 15;
         case NEG : return 20;
         case AND : return 25;
         case XOR : return 30;
         case IMP : return 35;
         case IFF : return 40;
         case OR:   return 45;
         case U:    return 50;
         case DU:   return 55;
         case S:    return 60;
         case DS:   return 65;
         case X:    return 70;
         case DX:   return 75;
         case Y:    return 80;
         case DY:   return 85;
         case END:  return 90;
         default:   return 95;
       }
     }

}
