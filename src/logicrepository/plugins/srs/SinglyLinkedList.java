import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SinglyLinkedList<E> implements Iterable<E> {
  protected Node head;
  protected Node tail;
  protected int size;

  protected class Node {
    protected E element;
    protected Node next;

    protected Node() { element = null; next = null; }
    
    protected Node(E e){
      element = e;
      next = null;
    }

    protected Node(E e, Node next){
      element = e;
      this.next = next;
    }

    @Override
    public String toString(){
      return "<" + element + ", " + ((next == null)? "<>" : next.toString()) + ">";
    }
  }

  protected class SLLIterator implements Iterator<E> {
    protected Node nextNode;
    protected Node currentNode;
    protected Node previousNode;
    protected boolean legalState = false;

    protected SLLIterator(){
      nextNode = (Node) head;
      previousNode = null;
      currentNode = null;
    } 

    protected SLLIterator(Iterator<E> I){
      SLLIterator J; 
      try{
          J = (SLLIterator) I;
      } catch(ClassCastException e){
        throw new IllegalArgumentException("Not an SLLIterator");
      }
      nextNode = J.nextNode;
      currentNode = J.currentNode;
      previousNode = J.previousNode;
      legalState = J.legalState;
    }

    @Override
    public boolean hasNext(){
      return nextNode != null;
    }

    @Override
    public E next() {
      if(nextNode == null) {
        throw new java.util.NoSuchElementException();
      }
      legalState = true;
      E ret = nextNode.element;
      previousNode = currentNode;
      currentNode = nextNode;
      nextNode = nextNode.next;
      return ret;
    }

    @Override
    public void remove() {
      if(!legalState) {
        throw new IllegalStateException();
      } 
      legalState = false;
      if(currentNode == head){
        head = head.next; 
        --size;
        return;
      }
      if(currentNode == tail){
        tail = (Node) previousNode;
      }
      previousNode.next = nextNode;
      --size;
    }
  }

  public SinglyLinkedList () { head = null; }

  public SinglyLinkedList (Iterable<E> c) {
    Iterator<E> I = c.iterator();
    if(!I.hasNext()) {
      head = null;
      size = 0;
      return;
    }
    size = 1;
    head = new Node();
    head.element = I.next();
    Node node = head;
    while(I.hasNext()){
      ++size;
      node.next = new Node();
      node = node.next;
      node.element = I.next();
      node.next = null;
    }
    tail = node;
  }

  public boolean add(E e){
    tail.next = new Node(e);
    tail = tail.next;
    return true;
  }

  public boolean addAll(Iterable<E> c){
    Node node = tail;
    Iterator<? extends E> I = c.iterator();
    while(I.hasNext()){
      ++size;
      node.next = new Node();
      node = node.next;
      node.element = I.next();
      node.next = null;
    }
    tail = node;
    return true;
  }

  public boolean contains(Object o){
    return false;
  }

  @Override
  public boolean equals(Object o){
    if(o == this) return true;
    if(!(o instanceof SinglyLinkedList)) return false;
    SinglyLinkedList comp = (SinglyLinkedList) o; 
    if(size != comp.size) return false;
    Iterator<E> I = iterator();
    Iterator J    = comp.iterator();
    while(I.hasNext()){
      E o1 = I.next();
      Object o2 = J.next();
      if( !(o1==null ? o2==null : o1.equals(o2))) return false;  
    }
    return true;
  }

  @Override
  public int hashCode(){
    return size ^ head.element.hashCode() ^ tail.element.hashCode();
  }

  public boolean isEmpty(){
    return head == null;
  }

  public boolean remove(Object o){
    Iterator<E> I = iterator();
    while(I.hasNext()){
      E e = I.next();
      if(e.equals(o)){
        I.remove();
        return true;
      }
    }
    return false;
  }

  public int size(){
    return size;
  }

  //WARNING: This does NOT copy the replacement list, so this can be considered
  //destructive to the replacement list, as it will have a new tail after this.
  public void replace(Iterator<E> I, Iterator<E> J, SinglyLinkedList<E> replacement){
    SLLIterator H;
    SLLIterator T;
    try{
      H = (SLLIterator) I;
      T = (SLLIterator) J;
    } catch(ClassCastException e){
      throw new IllegalArgumentException(
          "replace can only accept Iterators from a SinglyLinkedList");
    }
    H.currentNode.next = replacement.head;
    H.nextNode = replacement.head;  
    Node node = replacement.head;
    while(node.next != null){
      node = node.next;
    }
    node.next = T.currentNode;
  }

  public void nonDestructiveReplace(Iterator<E> I, Iterator<E> J, SinglyLinkedList<E> replacement){
    SinglyLinkedList<E> clone = new SinglyLinkedList<E>(replacement);
    replace(I, J, clone);
  }

  @Override 
  public String toString(){
    StringBuilder sb = new StringBuilder("[");
    Node node = head;
    while(node != null){
      sb.append(node.element.toString());
      sb.append(", ");
      node = node.next;
    }
    //remove the last ", "
    String ret = sb.substring(0, sb.length() - 2);
    return ret + "]";
  }

  @Override
  public Iterator<E> iterator(){
    return new SLLIterator();
  }

  public Iterator<E> iterator(Iterator<E> I){
    return new SLLIterator(I);
  }


  public static void main(String[] args){
    String[] a 
      = new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"}; 
    ArrayList<String> arr = new ArrayList<String>();
    for(String s : a){
      arr.add(s);
    }
    SinglyLinkedList<String> l = new SinglyLinkedList<String>(arr);
    SinglyLinkedList<String> l2 = new SinglyLinkedList<String>(arr);
    System.out.println(l);
    for(String s : l){
      System.out.println(s);
    }
    
    System.out.println("------AL------");
    Iterator<String> I = arr.iterator();
    removeTest(I);
    System.out.println(arr + " " + arr.size());
    
    System.out.println("------SLL------");
    I = l.iterator();
    removeTest(I);
    System.out.println(l + " " + l.size() + " " + l.tail);

    System.out.println("replacing 3 -- 10 with 0, 0, 0, 0"); 
    ArrayList<String> t = new ArrayList<String>();
    for(int i = 0; i < 4; ++i){
      t.add("0");
    }
    SinglyLinkedList<String> replacement = new SinglyLinkedList<String>(t);
    Iterator<String> I3 = l2.iterator();
    while(I3.hasNext()){
      if(I3.next().equals("2")){
        System.out.println("found 2");
        break;
      }
    }

    Iterator<String> I10 = l2.iterator(I3);
    while(I10.hasNext()){
      if(I10.next().equals("11")){
        System.out.println("found 11");
        break;
      }
    }

    l2.replace(I3,I10,replacement);
    System.out.println(l2);

    System.out.println("Now make sure I3 is not broken");
    while(I3.hasNext()){
      System.out.println(I3.next());
    }

    System.out.println("Now make sure I10 is not broken");
    while(I10.hasNext()){
      System.out.println(I10.next());
    }

    System.out.println("Now make sure we can iterate over the whole new list");
    for(String s : l2){
      System.out.println(s);
    }
    System.out.println(l2.tail);

    for(int i = 0; i < 12; ++i){
      l2.add("foo");
    }
    System.out.println(l2.tail);
    System.out.println(l2.head);

    l2.addAll(arr);
    System.out.println(l2);
    System.out.println(l2.tail);

    l2.remove("foo");
    System.out.println(l2);
    l2.remove("foo");
    System.out.println(l2);

    l2.remove("11");
    System.out.println(l2);
    l2.remove("11");
    System.out.println(l2);
    System.out.println(l2.tail);
  }

  private static void removeTest(Iterator I){
    int i = 0;
    while(I.hasNext()){
      System.out.println(I.next());
      if((i & 1) == 0){
         I.remove();
      }
      ++i;
    }
  }
}
