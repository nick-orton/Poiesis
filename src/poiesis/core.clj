(ns poiesis.core
  (:use poiesis.forms)
  (:use poiesis.lexer)
  (:use poiesis.parser)
  (:use poiesis.evaluator)
  (:gen-class))


(defn compute [string]
  (str (evaluate (parse-l (lex string)))))

(defn prompt []
  (let [console (. System console)]
    (.readLine console "?" (to-array []) )))

(defn repl []
    (println "Welcome to the Poiesis Repl. Type \"exit\" to leave.")  
    (loop [input (prompt)]
      (if (= "exit" input)
        (println "bye")
        (do
          (println "->" (compute input))
          (recur (prompt))))))

(defn -main [& args]
    (if (empty? args)
      (repl)
      (println "->" (compute (apply str args)))))


