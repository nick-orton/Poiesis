(ns poiesis.forms)

(defprotocol Term
  (lambda? [expression])
  (word? [term]))

(defprotocol Expression
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
      (lambda? [_] (not (empty? bound-vars)))
    Expression
      (get-bound-vars [_] bound-vars)
      (get-terms [_] terms)
    Object
     (toString [self] 
               (if (lambda? self)
                 (str "(" "[" (apply str bound-vars) "]" (apply str terms) ")")
                 (str "(" (apply str terms) ")")))))

