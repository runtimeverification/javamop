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

  public SpliceList(Collection<E> c){
    for(E e : c){
      add(e);
    }
  }

  public SpliceList(SpliceList<E> c){
    SLIterator<E> I = c.head();
    do {
      add(I.get());
    } while(I.next());  
  }

  public SpliceList(E[] c){
    for(E e : c){
      add(e);
    }
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

  public void add(Collection<E> c){
    for(E e : c){
      add(e);
    }
  }

  public void add(E[] c){
    for(E e : c){
      add(e);
    }
  }

  @Override
  public String toString(){
    if(isEmpty()) return "#epsilon";
    Node current = head;
    StringBuilder sb = new StringBuilder("[");
    do {
     sb.append(current.element);
     sb.append(" ");
     current = current.next; 
    } while(current != null); 
    sb.setCharAt(sb.length() - 1, ']'); 
    return sb.toString();
  } 

  //C++ style iterator, because it's far superior
  public class SLIteratorImpl implements SLIterator<E> {
    protected Node node; 

    protected SLIteratorImpl(Node node){
      this.node = node;
    }

    @Override
    public boolean next(){
      if(node.next == null) return false;
      node = node.next;
      return true;
    }

    @Override
    public boolean next(int amount){
      for(int i = 0; i < amount; ++i){
        if(!next()) return false;
      }
      return true;
    }

    @Override
    public boolean previous(){
      if(node.prev == null) return false;
      node = node.prev;
      return true;
    }

    @Override
    public boolean previous(int amount){
      for(int i = 0; i < amount; ++i){
        if(!previous()) return false;
      }
      return true;
    }

    @Override
    public E get(){
      return node.element;
    }

    //WARNING:  This assumes iterators point to the same list!
    @Override
    public void splice(SLIterator<E> end, SpliceList<E> replacement){
      if(isEmpty()) {
        if(replacement.isEmpty()) return;
        head = replacement.head;
        tail = replacement.tail;
        return;
      }  
      SLIteratorImpl endImpl;
      try {
        endImpl = (SLIteratorImpl) end;
      }
      catch(ClassCastException e){
        throw new IllegalArgumentException("Must provide an SLIteratorImpl to splice");
      }
      if(replacement.isEmpty()){
        spliceEmptyRepl(endImpl);
        return;
      }
    }

    private void spliceEmptyRepl(SLIteratorImpl endImpl){
      if(node == head){
        head = endImpl.node.next; 
        head.prev = null;
        node = head;
        endImpl.node = head;
      }
      else if(node == tail){
        tail = tail.prev;
        tail.next = null;
        node = null;
        endImpl.node = null;
      }
      else {
        Node prev = node.prev;
        Node next = endImpl.node.next;
        prev.next = next;
        if(next != null) next.prev = prev;
        node = next;
        endImpl.node = next;
      }
      if (endImpl.node == tail) {
        tail = node; 
      }
    }
    
    @Override
    public void nonDestructiveSplice(SLIterator<E> end, SpliceList<E> replacement){
      splice(end, new SpliceList<E>(replacement));
    }

    @Override
    public void nonDestructiveSplice(SLIterator<E> end, Collection<E> replacement){
      splice(end, new SpliceList<E>(replacement));
    }

    @Override 
    public String toString(){
      if(node == null) return "<>";
      return node.toString();
    }

    @Override
    public boolean equals(Object o){
      if(this == o) return true;
      SLIteratorImpl other;
      try{
         other = (SLIteratorImpl) o;
      }
      catch(ClassCastException e){
        return false;
      }
      return   (node.element == other.node.element) 
            && (node.next    == other.node.next   )
            && (node.prev    == other.node.prev   );
    }
  }

  public SLIterator<E> head(){
    return new SLIteratorImpl(head);
  }

  public SLIterator<E> tail(){
    return new SLIteratorImpl(tail);
  }

  public static void main(String[] args){
    SpliceList<String> empty = new SpliceList<String>();

    SpliceList<String> sl = new SpliceList<String>(args);
    SpliceList<String> sl2 = new SpliceList<String>(sl);
    SpliceList<String> sl3 = new SpliceList<String>(sl);
    SpliceList<String> sl4 = new SpliceList<String>(sl);
    SpliceList<String> sl5 = new SpliceList<String>(sl);
    SpliceList<String> sl6 = new SpliceList<String>(sl);

    SLIterator<String> H;
    SLIterator<String> T;

    System.out.println(sl.head);
    System.out.println(sl.tail);
    System.out.println(sl);
    H = sl.head();
    do {
      System.out.println(H);
    } while(H.next());
    H = sl.head();
    do {
      System.out.println(H.get());
    } while(H.next());

    H = empty.head();
    T = empty.tail();
    System.out.println("========splicing empty to empty========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(empty);
    H.splice(T, new SpliceList<String>());    
    System.out.println(H);
    System.out.println(T);
    System.out.println(empty);


    H = empty.head();
    T = empty.tail();
    System.out.println("========splicing [1 2 3] to empty========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(empty);
    H.splice(T, new SpliceList<String>(new String[] {"1", "2", "3"}));    
    System.out.println(H);
    System.out.println(T);
    System.out.println(empty);


    H = sl.head();
    T = sl.tail();
    T.previous(5);
    System.out.println("========splicing empty to front========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl);
    H.splice(T, new SpliceList<String>());
    System.out.println("========done========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl);


    H = sl2.head();
    T = sl2.head();
    System.out.println("========splicing empty to single front element========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl2);
    H.splice(T, new SpliceList<String>());
    System.out.println("========done========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl2);


    H = sl3.head();
    T = sl3.tail();
    H.next(2);
    T.previous(5);
    System.out.println("========splicing empty to middle========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl3);
    H.splice(T, new SpliceList<String>());
    System.out.println("========done========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl3);

    H = sl4.head();
    H.next(5);
    T = sl4.tail();
    System.out.println("========splicing empty to back========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl4);
    H.splice(T, new SpliceList<String>());
    System.out.println("========done========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl4);

    H = sl5.tail();
    T = sl5.tail();
    System.out.println("========splicing empty to single back element========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl5);
    H.splice(T, new SpliceList<String>());
    System.out.println("========done========");
    System.out.println(H);
    System.out.println(T);
    System.out.println(sl5);
  }
}

