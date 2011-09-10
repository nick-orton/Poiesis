(ns poiesis.test.lexer
  (:use [poiesis.lexer])
  (:use [clojure.test]))


(deftest test-build-parse-seq
  (is (= '("AB")  (build-parse-seq '("A" "B"))))
  (is (= '("(" "AB" ")" (build-parse-seq '( "(" "A" "B" ")")))))
  (is (= '("(" "[" "AB" "]" "CD" ")" (build-parse-seq '( "(" "[" "A" "B" "]" "C" "D" ")"))))))
