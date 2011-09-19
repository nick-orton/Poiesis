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
  (is (= "([:C]:A:B)" (str (first (cons-expr '(:B :A (:LAMBDA-BINDING :C) "(") )))))
  (is (= ":D" (str (second (cons-expr '(:B :A (:LAMBDA-BINDING :C) "(" :D) ))))))

(deftest test-parse-l
  (is (= "( A B)"  (str (first (parse-l '("(" "A" "B" ")") {})))))
  (is (= "( A B)"  (str  (first(parse-l '("(" "A" "B" ")") {})))))
  (is (= "( A B)"  (str (first (parse-l '( "A" "B") {})))))
  (is (= "([ A] A B)"  (str (first (parse-l '("(" "[""A" "]" "A" "B" ")") {})))))
  (is (= "([ A]( A) B)"  
         (str (first (parse-l '("(" "[""A" "]" "(" "A" ")" "B" ")") {})))))
  (is (= "([ A]([ A] A) B)" 
         (str (first (parse-l '("(" "[""A" "]" "(" "[" "A" "]" "A" ")" "B" ")") {}))))))

(deftest words-are-flyweights
  (def pair-of-words  (get-terms (first (parse-l '("A" "A") {}))))
  (is (= (first pair-of-words) (second pair-of-words)))
  (def A (make-word "A"))
  (is (= A (first (parse-l '("A") {"A" A}))))
  (is (= #{"A" "B"} (set(keys (second (parse-l '("A" "B") {"A" A})))))))

