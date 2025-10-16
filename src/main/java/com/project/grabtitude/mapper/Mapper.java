package com.project.grabtitude.mapper;

public interface Mapper<A, B>{
    B mapTo(A a);
    A mapFrom(B b);
}
