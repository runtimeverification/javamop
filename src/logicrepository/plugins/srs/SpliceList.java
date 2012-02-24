package logicrepository.plugins.srs;

import java.util.Collection;

public class SpliceList<E> {

  protected class Node {
    protected E element;
    protected Node prev = null;
    protected Node next = null;

    protected Node(){ 
      element = null; 
    }

    protected Node(E element){
      this.element = element;
    }

    @Override
    public String toString(){
      StringBuilder sb = new StringBuilder("<");
      sb.append((prev == null)?"<>":prev.toStringPrev());
      sb.append(" [");
      sb.append(element);
      sb.append("] "); 
      sb.append((next == null)?"<>":next.toStringNext());
      sb.append(">");
      return sb.toString();
    }

    private StringBuilder toStringPrev(){
      StringBuilder sb = new StringBuilder((prev == null)?"<>":prev.toStringPrev());
      sb.append(" ");
      sb.append(element);
      return sb;
    }

    private StringBuilder toStringNext(){
      StringBuilder sb = new StringBuilder(element.toString());
      sb.append(" ");
      sb.append((next == null)?"<>":next.toStringNext());
      return sb;
    }
  }

  private Node head;
  private Node tail;

  public SpliceList(){
    head = tail = null;
  }

  public boolean isEmpty(){
    return head == null;
  }

  public void add(E element){
    if(head == null){
      head = tail = new Node(element);
      return;
    } 
    tail.next = new Node(element);
    tail.next.prev = tail;
    tail = tail.next;
  }


  protected class SLIterator {

  }

  public static void main(String[] args){
    SpliceList<String> sl = new SpliceList<String>();
    for(String s : args){
      sl.add(s);
    }
    System.out.println(sl.head);
    System.out.println(sl.tail);
  }
}
