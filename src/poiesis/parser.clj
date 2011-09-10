(ns poiesis.parser
  (:use poiesis.forms))


(defn build-expression [expr-stack]
  (if (empty? expr-stack)
      (make-lambda '() '())
      (if (and (seq? (first expr-stack)) (= (ffirst expr-stack) :LAMBDA-BINDING))
        (make-lambda (rest (first expr-stack)) (rest expr-stack))
        (make-lambda '() expr-stack))))

(defn cons-lambda-bindings [expr-stack]
  (loop [e-stack expr-stack
         l-stack '()]
    (if (empty? e-stack)
        (throw (RuntimeException. "Parse Exception.  Could not find start symbol for variable bindings"))
        (let [sym (first e-stack)]
          (if (= "[" sym)
            (cons (cons :LAMBDA-BINDING l-stack) (rest e-stack))
            (recur (rest e-stack) (cons sym l-stack)))))))
