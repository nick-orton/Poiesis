(ns poiesis.lexer)


(def special-chars #{"(" ")" "[" "]"})

(defn- cons-stack-on [stack target]
  (concat  target (conj '() (apply str (reverse stack)))))

(defn build-parse-seq [char-seq]
  (loop [c-seq char-seq
         p-seq '()
         stack '()]
    (if (empty? c-seq)
      (if (empty? stack)
        p-seq
        (cons-stack-on stack p-seq))
      (let [sym (first c-seq)]
        (cond 
          (contains? special-chars sym)
            (recur (rest c-seq) (cons sym (cons-stack-on stack p-seq)) '())
          (= " " sym)
            (recur (rest c-seq) (cons-stack-on stack p-seq) '())
          :else (recur (rest c-seq) p-seq (cons sym stack)))))))


