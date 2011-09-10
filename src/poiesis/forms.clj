(ns poiesis.forms)

(defprotocol Term
  (atomic? [term]))

(defprotocol Expression
  (lambda? [expression])
  (get-bound-vars [lambda])
  (get-terms [expression]))

(defprotocol Atom
  (bound-by? [self expression])
  (get-sym [self])
  (eq? [self other]))


(defn make-atom [sym]
  (reify
    Term
    (atomic? [_] true)
    Atom
     (get-sym [_] sym)
     (eq? [_ other](= sym (get-sym other)))
     (bound-by? [self lambda]
          (some #(eq? self %) (get-bound-vars lambda)))
    Object
      (toString [_] (str " " sym))))

(defn make-lambda [bound-vars terms]
  (reify
    Term
      (atomic? [_] false)
    Expression
      (lambda? [_] (not (empty? bound-vars)))
      (get-bound-vars [_] bound-vars)
      (get-terms [_] terms)
    Object
     (toString [_] (str "(λ" (apply str bound-vars) "." (apply str terms) ")"))))

