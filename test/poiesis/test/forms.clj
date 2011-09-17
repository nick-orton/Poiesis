(ns poiesis.test.forms
  (:use [poiesis.forms])
  (:use [clojure.test]))


(def foo (make-word "foo"))

(def lambda-expr (make-lambda [foo] []))
(def empty-expression (make-lambda [] []))
(def not-a-lambda (make-lambda [] [foo]))

(deftest atoms-are-atomic 
  (is (word? foo)))

(deftest atoms-are-not-lambdas
  (is (not (lambda? foo))))

(deftest atoms-have-syms
  (is (= "foo" (get-sym foo))))

(deftest atoms-are-eq-if-their-syms-are-equal
  (is (eq? (make-word "foo") foo))
  (is (not (eq? (make-word "bar") foo))))

(deftest atoms-are-bound-by-an-expression-if-they-are-in-the-bound-vars
  (is (bound-by? foo lambda-expr))
  (is (not (bound-by? foo not-a-lambda))))

(deftest expressions-are-not-atomic
  (is (not (word? lambda-expr))))

(deftest expressions-are-lambdas-if-they-bind-variables
  (is (lambda? lambda-expr))
  (is (not (lambda? empty-expression))))

(deftest can-get-bound-vars
  (is (= [foo] (get-bound-vars lambda-expr)))
  (is (= [] (get-bound-vars not-a-lambda))))

(deftest can-get-terms
  (is (= [] (get-terms lambda-expr)))
  (is (= [foo] (get-terms not-a-lambda))))

