(ns poiesis.parser
  (:use poiesis.forms))


(defn build-expression [expr-stack]
  (if (empty? expr-stack)
      (make-lambda '() '())
      (if (and (seq? (first expr-stack)) (= (ffirst expr-stack) :LAMBDA-BINDING))
        (make-lambda (rest (first expr-stack)) (rest expr-stack))
        (make-lambda '() expr-stack))))

(defn cons-lambda-bindings [stack]
  (loop [e-stack stack
         l-stack '()]
    (if (empty? e-stack)
        (throw (RuntimeException. "Parse Exception.  Could not find start 
                                   symbol for variable bindings"))
        (let [sym (first e-stack)]
          (if (= "[" sym)
            (cons (cons :LAMBDA-BINDING l-stack) (rest e-stack))
            (recur (rest e-stack) (cons sym l-stack)))))))

(defn cons-expr [stack]
  (loop [e-stack '()
         p-stack stack]
    (if (empty? p-stack)
        (throw (RuntimeException. "Parse Exception.  Could not find start 
                                    symbol for expression" ))
        (let [sym (first p-stack)]
          (if (= sym "(")
            (cons (build-expression e-stack) (rest p-stack))
            (recur (cons sym e-stack)  (rest p-stack))))))) 

(defn parse-l [symbols]
  (loop [syms symbols
         stack '()
         atoms {}]
    (if (empty? syms)
      (if (= 1 (count stack))
        (first stack)
        (make-lambda '() (reverse stack)))
      (let [sym (first syms)]
        (cond 
          (= ")" sym) (recur (rest syms) (cons-expr stack) atoms) 
          (= "]" sym) (recur (rest syms) (cons-lambda-bindings stack) atoms)
          (or (= "[" sym) (= "(" sym)) (recur (rest syms) (cons sym stack) atoms)
          :else 
            (let [existing  (atoms sym)
                  is-new-atom? (nil? existing)
                  atm (if is-new-atom? 
                          (make-atom sym)
                          existing)
                  atoms* (if is-new-atom?
                           (assoc atoms sym atm)
                           atoms)]
            (recur (rest syms) (cons atm stack) atoms*)))))))
