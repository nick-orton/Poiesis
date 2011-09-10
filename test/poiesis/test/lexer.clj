(ns poiesis.test.lexer
  (:use [poiesis.lexer])
  (:use [clojure.test]))


(deftest test-build-parse-seq
  (is (= ["AB"]  (build-parse-seq '("A" "B"))))
  (is (= ["(" "AB" ")"] (build-parse-seq '( "(" "A" "B" ")"))))
  (is (= ["XY" "(" "AB" ")" "MN"] (build-parse-seq '( "X" "Y" "(" "A" "B" ")" "M" "N"))))
  (is (= ["AB" "CD" ] (build-parse-seq '( "A" "B" " " "C" "D"))))
  (is (= ["AB" "CD" ] (build-parse-seq '( "A" "B" " " " " "C" "D"))))
  (is (= ["(" "[" "AB" "]" "CD" ")"] (build-parse-seq '( "(" "[" "A" "B" "]" "C" "D" ")"))))
         )

(deftest test-lex
  (is (= ["AB"]  (lex "AB")))
  (is (= ["(" "AB" ")"] (lex  "(AB)")))
  (is (= ["XY" "(" "AB" ")" "MN"] (lex  "XY(AB)MN")))
  (is (= ["AB" "CD" ] (lex  "AB CD")))
  (is (= ["AB" "CD" ] (lex  "AB  CD")))
  (is (= ["(" "[" "AB" "]" "CD" ")"] (lex  "([AB]CD)"))))
