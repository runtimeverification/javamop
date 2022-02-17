package com.runtimeverification.rvmonitor.logicrepository.plugins.srs;

import java.util.Collection;

public class SpliceList<E> {
    
    protected class Node {
        protected E element;
        protected Node prev = null;
        protected Node next = null;
        
        protected Node() { 
            element = null; 
        }
        
        protected Node(E element) {
            this.element = element;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("<");
            sb.append((prev == null)?"<>":prev.toStringPrev());
            sb.append(" [");
            sb.append(element);
            sb.append("] "); 
            sb.append((next == null)?"<>":next.toStringNext());
            sb.append(">");
            return sb.toString();
        }
        
        private StringBuilder toStringPrev() {
            StringBuilder sb = new StringBuilder((prev == null)?"<>":prev.toStringPrev());
            sb.append(" ");
            sb.append(element);
            return sb;
        }
        
        private StringBuilder toStringNext() {
            StringBuilder sb = new StringBuilder(element.toString());
            sb.append(" ");
            sb.append((next == null)?"<>":next.toStringNext());
            return sb;
        }
    }
    
    private Node head;
    private Node tail;
    
    public SpliceList() {
        head = tail = null;
    }
    
    public SpliceList(Collection<E> c) {
        for(E e : c) {
            add(e);
        }
    }
    
    public SpliceList(SpliceList<E> c) {
        SLIterator<E> I = c.head();
        do {
            add(I.get());
        } while(I.next());  
    }
    
    public SpliceList(E[] c) {
        for(E e : c) {
            add(e);
        }
    }
    
    public boolean isEmpty() {
        return head == null;
    }
    
    public void add(E element) {
        if(head == null) {
            head = tail = new Node(element);
            return;
        } 
        tail.next = new Node(element);
        tail.next.prev = tail;
        tail = tail.next;
    }
    
    public void add(Collection<E> c) {
        for(E e : c) {
            add(e);
        }
    }
    
    public void add(E[] c) {
        for(E e : c) {
            add(e);
        }
    }
    
    @Override
    public String toString() {
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
        
        protected SLIteratorImpl(Node node) {
            this.node = node;
        }
        
        @Override
        public SLIterator<E> copy() {
            return new SLIteratorImpl(node);
        }
        
        @Override
        public boolean next() {
            if(node == null || node.next == null) return false;
            node = node.next;
            return true;
        }
        
        @Override
        public boolean next(int amount) {
            for(int i = 0; i < amount; ++i) {
                if(!next()) return false;
            }
            return true;
        }
        
        @Override
        public boolean previous() {
            if(node.prev == null) return false;
            node = node.prev;
            return true;
        }
        
        @Override
        public boolean previous(int amount) {
            for(int i = 0; i < amount; ++i) {
                if(!previous()) return false;
            }
            return true;
        }
        
        @Override
        public E get() {
            if(node == null) return null;
            return node.element;
        }
        
        //WARNING:  This assumes iterators point to the same list!
        @Override
        public void splice(SLIterator<E> end, SpliceList<E> replacement) {
            SLIteratorImpl endImpl;
            try {
                endImpl = (SLIteratorImpl) end;
            }
            catch(ClassCastException e) {
                throw new IllegalArgumentException("Must provide an SLIteratorImpl to splice");
            }
            if(isEmpty()) {
                if(replacement.isEmpty()) return;
                head = replacement.head;
                tail = replacement.tail;
                node = head;
                endImpl.node = head;
                return;
            }  
            //we are splicing empty into something not empty
            if(replacement.isEmpty()) {
                spliceEmptyRepl(endImpl);
                return;
            }
            //we are splicing something not empty into something not empty
            spliceNonEmptyRepl(endImpl, replacement);
        }
        
        private void spliceEmptyRepl(SLIteratorImpl endImpl) {
            if(node == head) {
                if(endImpl.node == tail) {
                    head = tail = node = endImpl.node = null;
                    return;
                }
                head = endImpl.node.next; 
                head.prev = null;
                node = head;
                endImpl.node = head;
                return;
            }
            if(node == tail) {
                tail = tail.prev;
                tail.next = null;
                node = null;
                endImpl.node = null;
                return;
            } 
            Node prev = node.prev;
            Node next = endImpl.node.next;
            prev.next = next;
            if(next != null) next.prev = prev;
            node = next;
            if (endImpl.node == tail) {
                tail = prev; 
            }
            endImpl.node = next;
        }
        
        private void spliceNonEmptyRepl(SLIteratorImpl endImpl, SpliceList<E> replacement) {
            if(node == head) {
                if(endImpl.node == tail) {
                    head = replacement.head;
                    tail = replacement.tail;
                    node = replacement.head;
                    replacement.head = replacement.tail = endImpl.node = null;
                    return;
                }
                head = replacement.head;
                node = head;
                replacement.head = null;
                endImpl.node = endImpl.node.next;
                replacement.tail.next = endImpl.node; 
                endImpl.node.prev = replacement.tail;
                replacement.tail = null;
                return;
            }    
            if(node == tail) {
                endImpl.node = null;
                tail = replacement.tail;
                node.prev.next = replacement.head;
                replacement.head.prev = node.prev;
                node = replacement.head;
                replacement.tail = replacement.head = null;
                return;
            }
            node.prev.next = replacement.head;
            replacement.head.prev = node.prev;
            
            replacement.tail.next = endImpl.node.next;
            if(endImpl.node.next != null) {
                endImpl.node.next.prev = replacement.tail;
            }
            if(endImpl.node == tail) {
                tail = replacement.tail; 
            }
            
            node = replacement.head;
            endImpl.node = replacement.tail.next;
            replacement.head = replacement.tail = null;
        }
        
        @Override
        public void nonDestructiveSplice(SLIterator<E> end, SpliceList<E> replacement) {
            splice(end, new SpliceList<E>(replacement));
        }
        
        @Override
        public void nonDestructiveSplice(SLIterator<E> end, Collection<E> replacement) {
            splice(end, new SpliceList<E>(replacement));
        }
        
        @Override 
        public String toString() {
            if(node == null) return "<>";
            return node.toString();
        }
        
        @Override
        public boolean equals(Object o) {
            if(o == null) return false;
            if(this == o) return true;
            SLIteratorImpl other;
            try{
                other = (SLIteratorImpl) o;
            }
            catch(ClassCastException e) {
                return false;
            }
            if(node == null) {
                if(other.node == null) return true;
                else return false;
            }
            if(other.node == null) return false;
            return   (node.element == other.node.element) 
            && (node.next    == other.node.next   )
            && (node.prev    == other.node.prev   );
        }
    }
    
    public SLIterator<E> head() {
        return new SLIteratorImpl(head);
    }
    
    public SLIterator<E> tail() {
        return new SLIteratorImpl(tail);
    }
}

