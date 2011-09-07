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
    (atomic? [_] true)))

