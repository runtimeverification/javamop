package logicrepository.plugins.ere;

public enum EREType {
	  EMP,  //empty
	  EPS,  //epsilon
     S,    //atom
     NEG,  //negation
     CAT,  //concat
	  STAR, //kleene closure
	  OR;   //or

	  public String toString(){
       switch(this) {
			case EMP :  return "empty";
			case EPS :  return "espilon";
		   case S :    return "symbol";
			case NEG :  return "~";
			case CAT :  return "cat";
			case STAR : return "*";
			case OR :   return "|";
			default:    return "error";
		 }
	  }

	   public int toInt(){
       switch(this) {
         case S :    return 1;
			case NEG :  return 10;
			case CAT :  return 100;
			case STAR : return 1000;
			case OR :   return 10000;
			default:    return 100000;
		 }
	  }

}
