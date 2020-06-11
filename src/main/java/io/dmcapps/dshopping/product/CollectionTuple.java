package io.dmcapps.dshopping.product;

public class CollectionTuple<X, Y> { 
    public final X name; 
    public final Y file; 

    public CollectionTuple(X name, Y file) { 
      this.name = name; 
      this.file = file; 
    } 
  } 