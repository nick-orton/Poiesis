(ns poiesis.lexer)


(def special-chars #{"(" ")" "[" "]"})
(def white-space #{" " ""})

(defn- cons-stack-on [stack target]
  (concat  target (conj '() (apply str (reverse stack)))))

(defn build-parse-seq [char-seq]
  (filter #(not (contains? white-space %)) 
  (loop [c-seq char-seq
         p-seq '()
         stack '()]
    (if (empty? c-seq)
      (if (empty? stack)
        p-seq
        (cons-stack-on stack p-seq))
      (let [sym (first c-seq)]
        (if 
          (or (contains? white-space sym) (contains? special-chars sym))
            (recur (rest c-seq) (cons sym (cons-stack-on stack p-seq)) '())
            (recur (rest c-seq) p-seq (cons sym stack))))))))


