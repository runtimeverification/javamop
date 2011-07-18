/*
  Some tests for SRSBizNiz
*/
package javamop.util.srs;

class Test {
  private static void test1() {
    header("Test1");
    SRSBizNiz srs = new SRSBizNiz();
    srs.append("abcdefghijk");
    srs.print();
    srs.append('l');
    srs.print();
    srs.addRule("b","<_>");
    srs.addRule("c","<^>");
    srs.print();
    srs.toNormalForm();
    srs.print();
  }

  // HasNext
  private static void test2() {
    header("Test2");
    SRSBizNiz srs = new SRSBizNiz();
    srs.append("hnhnhnhnhhhhnhnh" + "nn" + "hhnhnhn");
    srs.print();
    srs.addRule("nn","<FAIL>");
    srs.addRule(".*<FAIL>.*","<FAIL>");
    srs.addRule("hh","h");
    srs.addRule("hn","");
    srs.print();
    srs.toNormalForm();
    srs.print();
  }

  // HasNext, but starting with a next
  private static void test3() {
    header("Test3");
    SRSBizNiz srs = new SRSBizNiz();
    srs.append("nhnhhnhhhnhnh");
    srs.print();
    srs.addRule("^n","<FAIL>");
    srs.addRule("nn","<FAIL>");
    srs.addRule(".*<FAIL>.*","<FAIL>");
    srs.addRule("hh","h");
    srs.addRule("hn","");
    srs.print();
    srs.toNormalForm();
    srs.print();
  }

  // Correct HasNext
  private static void test4() {
    header("Test3");
    SRSBizNiz srs = new SRSBizNiz();
    srs.append("hnhhhnhhhnhnhh");
    srs.print();
    srs.addRule("^n","<FAIL>");
    srs.addRule("nn","<FAIL>");
    srs.addRule(".*<FAIL>.*","<FAIL>");
    srs.addRule("hh","h");
    srs.addRule("hn","");
    srs.addRule("^h$", "<SUCCESS>");
    srs.addRule("^$", "<SUCCESS>");
    srs.print();
    srs.toNormalForm();
    srs.print();
  }


  private static void header(String s) {
    System.out.println("");
    System.out.println("------");
    System.out.println(s);
    System.out.println("------");
    System.out.println("");
  }

  public static void main(String args[]) {
    test1();
    test2();
    test3();
    test4();
  }
}

