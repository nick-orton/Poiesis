(ns poiesis.lexer)


(def special-chars #{"(" ")" "[" "]"})

(defn- cons-stack-on [stack target]
  (cons (apply str (reverse stack)) target))

(defn build-parse-seq [char-seq]
  (loop [c-seq char-seq
         p-seq '()
         stack '()]
    (if (empty? c-seq)
      (if (empty? stack)
        p-seq
        (cons-stack-on stack p-seq))
      (let [sym (first c-seq)]
        (if (contains? special-chars sym)
          (recur (rest c-seq) (cons sym (cons-stack-on stack p-seq)) '())
          (recur (rest c-seq) p-seq (cons sym stack)))))))


