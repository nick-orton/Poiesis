(ns poiesis.test.parser
  (:use [poiesis.parser])
  (:use [poiesis.forms])
  (:use [clojure.test]))

(deftest test-build-expr
  (is (lambda? (build-expression [[:LAMBDA-BINDING :A :B] :C :D ]))) 
         )

