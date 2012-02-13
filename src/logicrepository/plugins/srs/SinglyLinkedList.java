import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SinglyLinkedList<E> implements Iterable<E> {
  protected Node<E> head;
  protected int size;

  protected class Node<E> {
    protected E element;
    protected Node<E> next;

    protected Node() { element = null; next = null; }
    
    protected Node(E e){
      element = e;
      next = null;
    }

    protected Node(E e, Node<E> next){
      element = e;
      this.next = next;
    }
  }

  protected class SLLIterator<E> implements Iterator<E> {
    protected Node<E> nextNode;
    protected Node<E> currentNode;
    protected Node<E> previousNode;
    protected boolean legalState = false;

    protected SLLIterator(){
      nextNode = (Node<E>) head;
      previousNode = null;
      currentNode = null;
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
      previousNode.next = nextNode;
      --size;
    }
  }

  public SinglyLinkedList () { head = null; }

  public SinglyLinkedList (Collection<E> c) {
    if(c.size() == 0) {
      head = null;
      size = 0;
      return;
    }
    size = 1;
    head = new Node<E>();
    Iterator<E> I = c.iterator();
    head.element = I.next();
    Node<E> node = head;
    while(I.hasNext()){
      ++size;
      node.next = new Node<E>();
      node = node.next;
      node.element = I.next();
      node.next = null;
    }
  }

  public int size(){
    return size;
  }

  //WARNING: This does NOT copy the replacement list, so this can be considered
  //destructive to the replacement list, as it will have a new tail after this.
  public void replace(Iterator<E> I, Iterator<E> J, SinglyLinkedList<E> replacement){
    if(!(I instanceof SLLIterator) || !(J instanceof SLLIterator)) {
      throw new IllegalArgumentException();
    }
    SLLIterator<E> H = (SLLIterator<E>) I;
    SLLIterator<E> T = (SLLIterator<E>) J;
    H.currentNode.next = replacement.head;
    H.nextNode = replacement.head;  
    Node<E> node = replacement.head;
    while(node.next != null){
      node = node.next;
    }
    node.next = T.currentNode;
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
    return new SLLIterator<E>();
  }


  public static void main(String[] args){
    ArrayList<String> arr = new ArrayList<String>();
    for(String s : args){
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
    System.out.println(l + " " + l.size());

    System.out.println("replacing 3 -- 10 with 0, 0, 0, 0"); 
    ArrayList<String> t = new ArrayList<String>();
    for(int i = 0; i < 4; ++i){
      t.add("0");
    }
    SinglyLinkedList<String> replacement = new SinglyLinkedList<String>(t);
    Iterator<String> I3 = l2.iterator();
    Iterator<String> I10 = l2.iterator();
    while(I3.hasNext()){
      if(I3.next().equals("2")){
        System.out.println("found 2");
        break;
      }
    }

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
