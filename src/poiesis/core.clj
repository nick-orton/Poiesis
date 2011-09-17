(ns poiesis.core
  (:use poiesis.forms)
  (:use poiesis.lexer)
  (:use poiesis.parser)
  (:use poiesis.evaluator)
  (:gen-class))

;TODO add dictionary and context
;     parser takes dictionary and returns words to add to it
;     evaluator takes context
(defn- compute [string context dictionary]
  (let [parsed (parse-l (lex string) @dictionary)
        expression (first parsed)
        dictionary* (second parsed)]
;TODO reset dictionary with second from parsed
  (swap! dictionary (fn [_] dictionary*))
  (evaluate expression context)))

(defn prompt []
  (let [console (. System console)]
    (.readLine console "?" (to-array []) )))

(defn repl []
    (println "Welcome to the Poiesis Repl. Type \"exit\" to leave.")  
    (let [context (atom {})
          dictionary (atom {})]
    (loop [input (prompt)
           number 0]
      (if (= "exit" input)
        (println "bye")
        (let [computation (compute input @context dictionary)
              sym (str "$" number)
              word (make-word sym)]
          (swap! dictionary assoc sym word)
          (swap! context assoc word computation)  
          (println (str word ">" computation))
          (recur (prompt) (inc number)))))))

(defn -main [& args]
    (if (empty? args)
      (repl)
      (println "->" (compute (apply str args)))))


