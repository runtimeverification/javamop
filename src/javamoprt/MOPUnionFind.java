package javamoprt;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Arrays;

public class MOPUnionFind<T> implements MOPObject{

  class MOPUnionFindNode<T>{
    private T el;
    private MOPUnionFindNode<T> parent;
    private int rank = 0;

	public T get(){
      return el;
	}
  }
 
  private Map<T, MOPUnionFindNode<T>> refs = new HashMap<T, MOPUnionFindNode<T>>();

  public MOPUnionFindNode<T> makeSet(T element){
	if(refs.containsKey(element)){
      return refs.get(element);
	}
	MOPUnionFindNode<T> n = new MOPUnionFindNode<T>();
    n.el = element;
	n.parent = n;
	refs.put(element, n);
	return n;
  }

  public MOPUnionFindNode<T> find(T x){
    return find(makeSet(x));
  }

  //we do path compression to flatten the tree
  public MOPUnionFindNode<T> find(MOPUnionFindNode<T> x){
	if(x.parent == x)
      return x;
	x.parent = find(x.parent);
    return x.parent;
  }

  public void union(Collection<T> col){
	Iterator<T> it = col.iterator();
	if(!it.hasNext()) return;
	T x = it.next();
	while(it.hasNext()){
      union(x, it.next());
	}
  }

  public void union(T x, T y){
    union(makeSet(x), makeSet(y)); 
  }

  public void union(MOPUnionFindNode<T> x, MOPUnionFindNode<T> y){
    MOPUnionFindNode<T> xRoot, yRoot;
    xRoot = find(x);
    yRoot = find(y);	
	//already in the same set
	if(xRoot == yRoot) return;

    if(xRoot.rank > yRoot.rank){
       yRoot.parent = xRoot;
	}
	else if(xRoot.rank < yRoot.rank){
       xRoot.parent = yRoot;
	}
	else { //same size, doesn't matter which we chose, but rank must increase
       yRoot.parent = xRoot;
	   ++xRoot.rank; 
	}
  }

  public boolean areConnected(Collection<T> col){
    Iterator<T> it = col.iterator();
	if(!it.hasNext()) return true; //really shouldn't be passing an empty collection
	T x = it.next();
	while(it.hasNext()){
      if(!areConnected(x, it.next())) return false;
	}
	return true;
  }

  public boolean areConnected(T x, T y){
    return areConnected(makeSet(x), makeSet(y));
  }

  public boolean areConnected(MOPUnionFindNode<T> x, MOPUnionFindNode<T> y){
    return find(x) == find(y);
  }

  /*
  public static void  main(String[] args){
    MOPUnionFind<Integer> uf = new MOPUnionFind<Integer>();
	uf.union(Arrays.asList(new Integer[]{1,2,3,4,5,6}));
	System.out.println("should be true: " 
		+ uf.areConnected(Arrays.asList(new Integer[] {1,2,3,4,5,6})));
	System.out.println("should be false: " 
		+ uf.areConnected(Arrays.asList(new Integer[] {1,2,3,4,5,6,7})));
	System.out.println("root1 " + uf.find(1).get()); 
	System.out.println("root2 " + uf.find(2).get()); 
	System.out.println("root3 " + uf.find(3).get()); 
	System.out.println("root4 " + uf.find(4).get()); 
	System.out.println("root5 " + uf.find(5).get()); 
	System.out.println("root6 " + uf.find(6).get()); 
	System.out.println("root7 " + uf.find(7).get()); 
  }*/
}

