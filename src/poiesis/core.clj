(ns poiesis.core
  (:use poiesis.forms)
  (:use poiesis.lexer)
  (:use poiesis.parser)
  (:use poiesis.evaluator)
  (:gen-class))


(defn compute [string]
  (str (evaluate (parse-l (lex string)))))

(defn -main [& args]
    (println "->" (compute (apply str args))))
