package de.hsfd.binarytreevis.services;

import java.lang.annotation.Repeatable;

@Repeatable(Authors.class)
public @interface Author{
    String name();
    String date();
}