(ns poiesis.core
  (:use poiesis.forms)
  (:use poiesis.lexer)
  (:use poiesis.parser)
  (:use poiesis.evaluator))


(defn compute [string]
  (str (evaluate (parse-l (lex string)))))
