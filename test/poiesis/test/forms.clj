(ns poiesis.test.forms
  (:use [poiesis.forms])
  (:use [clojure.test]))


(def foo (make-atom "foo"))

(def lambda-expr (make-lambda '(foo) '()))

(deftest atoms-are-atomic 
  (is (atomic? foo)))

(deftest atoms-have-syms
  (is (= "foo" (get-sym foo))))

(deftest atoms-are-eq-if-their-syms-are-equal
  (is (eq? (make-atom "foo") foo))
  (is (not (eq? (make-atom "bar") foo))))

;TODO bound-by

(deftest expressions-are-not-atomic
  (is (not (atomic? lambda-expr))))

