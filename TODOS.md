#TODOs

###Add a global context in a repl session

* when atom is evaluated for substitution, change if in context
* every top level expression evaluated in repl gets saved to an 
  sequential atom in context $1, $2, $3, etc

###Catch Exceptions

* give error message
* do not store in context

###Evaluate multiple lines

* on newline, if open parens matches closed parens, eval as expression
* otherwise get next line and concatinate to prev, do check again

###Long Atoms

* if an atom begins and ends with quotes, then it can have spaces and parens
  as characters in it.

----------------------

file evaluator

stin

stout

file in

file out

port in

port out
