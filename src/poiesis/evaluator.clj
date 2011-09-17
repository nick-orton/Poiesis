(ns poiesis.evaluator
  (:use poiesis.forms))


(declare replace-free evaluate eval-lambda evaluate*)

(defn substitute-if [context atm]
  (let [replacement (context atm)]
    (if (nil? replacement)
      atm
      replacement)))


(defn- replace-free*
  [context parent terms]
  (if (empty? terms)
    '()
    (let [term (first terms)
          head (cond 
                 (not (atomic? term)) (replace-free context term)
                 (bound-by? term parent)  term
                 :else (substitute-if context term))]
      (cons head (replace-free* context parent (rest terms))))))

(defn replace-free
  [context term]  
    (make-lambda 
      (get-bound-vars term) 
      (replace-free* context term (get-terms term))))


(defn simplify [bound-vars terms]
  (if (and (empty? bound-vars) (= 1 (count terms)))
    (first terms)
    (make-lambda bound-vars terms)))

(defn eval-expr [lambda]
  (simplify (get-bound-vars lambda) (evaluate* (get-terms lambda) {})))

(defn evaluate* [terms context]
  (if (empty? terms)
    '()
    (let [term (first terms)]
         (if (atomic? term)
           (cons term (evaluate* (rest terms) context))
           (if (lambda? term)
             (eval-lambda term (rest terms) context)
             (cons (eval-expr term)
                   (evaluate* (rest terms) context)))))))
    
(defn evaluate [term]
  (if (atomic? term)
    term
    (eval-expr term)))

(defn beta-reduce
  [lambda arg context]
  (let [vars (get-bound-vars lambda)
        context* (assoc context (first vars) arg)
        new-l (replace-free context* 
                (make-lambda (rest (get-bound-vars lambda)) 
                             (get-terms lambda)))]
       (simplify (get-bound-vars new-l) (get-terms new-l))))
 
(defn eval-lambda
  [l ts context]
  (loop [lambda l
         terms ts]
    (if (empty? terms)
      (cons (eval-expr lambda) '()) 
      (let [lambda* (evaluate (beta-reduce lambda (first terms) context))]
        (if (lambda? lambda*)
          (recur lambda* (rest terms))
          (cons lambda* (evaluate* (rest terms) context)))))))
               
