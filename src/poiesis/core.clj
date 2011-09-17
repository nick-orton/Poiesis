(ns poiesis.core
  (:use poiesis.forms)
  (:use poiesis.lexer)
  (:use poiesis.parser)
  (:use poiesis.evaluator)
  (:gen-class))

;TODO add dictionary and context
;     parser takes dictionary and returns words to add to it
;     evaluator takes context
(defn compute [string]
  (let [dictionary {}
        context {}
        parsed (parse-l (lex string) dictionary)
        expression (first parsed)]
;TODO reset dictionary with second from parsed

  (str (evaluate expression context))))

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


