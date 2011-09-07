(ns poiesis.test.forms
  (:use [poiesis.forms])
  (:use [clojure.test]))

(deftest atoms-are-atomic 
  (is (atomic? (make-atom "foo"))))
