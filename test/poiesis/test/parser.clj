(ns poiesis.test.parser
  (:use [poiesis.parser])
  (:use [poiesis.forms])
  (:use [clojure.test]))

(deftest test-build-expr
  (is (lambda? (build-expression '((:LAMBDA-BINDING :A :B) :C :D )))) 
  (is (lambda? (build-expression '((:LAMBDA-BINDING :A :B) :C :D )))) 
         )

(deftest test-cons-lambda-bindings
  (is (= '((:LAMBDA-BINDING :A :B)) (cons-lambda-bindings [:B :A "[" ])))
  (is (= '((:LAMBDA-BINDING :A :B) :C) (cons-lambda-bindings [:B :A "[" :C]))))


(deftest test-cons-expr
  (is (= "(:A:B)" (str (first (cons-expr '(:B :A "(") )))))
  (is (= "(λ:C.:A:B)" (str (first (cons-expr '(:B :A (:LAMBDA-BINDING :C) "(") )))))
  (is (= ":D" (str (second (cons-expr '(:B :A (:LAMBDA-BINDING :C) "(" :D) ))))))

(deftest test-parse-l
  (is (= "( A B)"  (str (parse-l '("(" "A" "B" ")") {}))))
  (is (= "( A B)"  (str (parse-l '("(" "A" "B" ")") {}))))
  (is (= "( A B)"  (str (parse-l '( "A" "B") {}))))
  (is (= "(λ A. A B)"  (str (parse-l '("(" "[""A" "]" "A" "B" ")") {}))))
  (is (= "(λ A.( A) B)"  
         (str (parse-l '("(" "[""A" "]" "(" "A" ")" "B" ")") {}))))
  (is (= "(λ A.(λ A. A) B)" 
         (str (parse-l '("(" "[""A" "]" "(" "[" "A" "]" "A" ")" "B" ")") {})))))

(deftest atoms-are-flyweights
  (def pair-of-atoms  (get-terms (parse-l '("A" "A") {})))
  (is (= (first pair-of-atoms) (second pair-of-atoms))))

