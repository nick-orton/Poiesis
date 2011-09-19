# Poïesis

From Wikipedia:

Poïesis is etymologically derived from the ancient Greek term ποιέω, which 
means "to make". This word, the root of our modern "poetry", was first a verb,
an action that transforms and continues the world. Neither technical production
nor creation in the romantic sense, poïetic work reconciles thought with matter
and time, and man with the world. 

Poiesis is a simple PL based on 
[lambda calculus](http://en.wikipedia.org/wiki/Lambda_calculus).

## Building

install [Leiningen](https://github.com/technomancy/leiningen)

    lein uberjar

## Usage

To enter the REPL

    java -jar poiesis-1.0.0-SNAPSHOT-standalone.jar    

To evaluate a single expression

    java -jar poiesis-1.0.0-SNAPSHOT-standalone.jar "expression"    

## Syntax & Semantics

An word is a string of characters not including ( ) [ ] or whitespace

    A-word 
    AN0Th3r+w0rD

A sequence of words is an expression.

    term1 term2

An expression may be bounded by parenthesis

    ( t1 t2 )

An expression can contain other expressions

    T1 ( T2 T3 T4 )

An expression may begin with word bindings.  This kind of expression is
called a Lambda

    ([X] X X)

In an expression, any terms to the right of a lambda are applied one at a time
to the lambda.  This is called beta reduction.
  
    ([X] X X) Y  
    ->  Y Y

    F ([X] X X) Y  
    ->  F (Y Y)

    ([X Y] X X) Z 
    -> ([Y] Z Z)

    ([X Y] X Y) Z A B  
    -> (Z A) B

    (([X Y] X Y ) Z ) A
    -> ([Y] Z Y) A
    -> (Z A)

Lambdas can have free variables.  In the Lambda below, X is bound and Y 
is free.

    ([X] X Y) Z
    -> (Z Y)

Free variable can be bound by an enclosing Lambda.  In the example below, X is
bound in the enclosing Lambda, but is free in the enclosed lambda.

    ([X] ([Y] Y X)) Z
    -> ([Y] Y Z)

## The REPL

Typing an expression into the "?" prompt will evaluate it

    ? ([x] x y)
    $0> ([ x] x y)

The result of the expression evaluated will be bound to a word starting with a
"$" and followed by a number representing the count of expressions evaluated so
far.  You can use this word in subsequent expressions as an alias to the
expression it is bound to.

    ? $0 ([z] z)
    $1> y
    ? $0 $1
    $2> ( y y)
   
Typing exit into the prompt will exit the REPL
  
    ?exit
    bye


## License

Copyright (C) 2011 Nick Orton

Distributed under the Eclipse Public License, the same as Clojure.
