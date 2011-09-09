(ns poiesis.parser
  (:use poiesis.forms))


(defn build-expression [expr-stack]
  (if (empty? expr-stack)
      (make-lambda '() '())
      (if (and (seq? (first expr-stack)) (= (ffirst expr-stack) :LAMBDA-BINDING))
        (make-lambda (rest (first expr-stack)) (rest expr-stack))
        (make-lambda '() expr-stack))))
