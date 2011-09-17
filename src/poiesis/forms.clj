(ns poiesis.forms)

(defprotocol Term
  (word? [term]))

(defprotocol Expression
  (lambda? [expression])
  (get-bound-vars [lambda])
  (get-terms [expression]))

(defprotocol Word
  (bound-by? [self expression])
  (get-sym [self])
  (eq? [self other]))


(defn make-word [sym]
  (reify
    Term
    (word? [_] true)
    Expression
     (lambda? [_] false)
    Word
     (get-sym [_] sym)
     (eq? [_ other](= sym (get-sym other)))
     (bound-by? [self lambda]
          (some #(eq? self %) (get-bound-vars lambda)))
    Object
      (toString [_] (str " " sym))))

(defn make-lambda [bound-vars terms]
  (reify
    Term
      (word? [_] false)
    Expression
      (lambda? [_] (not (empty? bound-vars)))
      (get-bound-vars [_] bound-vars)
      (get-terms [_] terms)
    Object
     (toString [self] 
               (if (lambda? self)
                 (str "(" "λ" (apply str bound-vars) "." (apply str terms) ")")
                 (str "(" (apply str terms) ")")))))

