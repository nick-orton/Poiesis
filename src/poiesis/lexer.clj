(ns poiesis.lexer)


(def special-chars #{"(" ")" "[" "]"})
(def white-space #{" " ""})

(defn- conj-stack-on [stack target]
  (conj  target (apply str (reverse stack))))

(defn build-parse-seq [char-seq]
  (filter #(not (contains? white-space %)) 
  (loop [c-seq char-seq
         p-seq []
         stack '()]
    (if (empty? c-seq)
      (if (empty? stack)
        p-seq
        (conj-stack-on stack p-seq))
      (let [sym (first c-seq)]
        (if 
          (or (contains? white-space sym) (contains? special-chars sym))
            (recur (rest c-seq) (conj (conj-stack-on stack p-seq) sym) '())
            (recur (rest c-seq) p-seq (cons sym stack))))))))


