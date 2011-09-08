(ns poiesis.evaluator
  (:use poiesis.forms))


(declare replace-free evaluate apply-lambda evaluate*)

(defn- replace-free*
  [variable arg parent terms]
  (if (empty? terms)
    '()
    (let [term (first terms)]
         (if (atomic? term)
           (if (and (not (bound-by? term parent))(eq? variable term))
               (cons arg (replace-free* variable arg parent (rest terms)))
               (cons term (replace-free* variable arg parent (rest terms))))
           (cons (replace-free variable arg term) 
                 (replace-free* variable arg parent (rest terms)))))))

(defn replace-free
  [variable arg term]
  (if (atomic? term) 
    term
    (make-lambda 
      (get-bound-vars term) 
      (replace-free* variable arg term (get-terms term)))))

(defn beta-reduce
  [variable arg terms]
  (if (empty? terms)
    '()
    (let [term (first terms)]
         (if (atomic? term)
           (if (eq? variable term)
             (cons arg (beta-reduce variable arg (rest terms)))
             (cons term (beta-reduce variable arg (rest terms))))
           (cons (replace-free variable arg term) (beta-reduce variable arg (rest terms)))))))


(defn simplify [bound-vars terms]
  (if (and (empty? bound-vars) (= 1 (count terms)))
    (first terms)
    (make-lambda bound-vars terms)))


(defn apply-var
  [lambda arg]
  (let [vars (get-bound-vars lambda)
        terms (get-terms lambda)]
       (if (empty? vars)
         lambda
         (let [variable (first vars)
               new-terms (beta-reduce variable arg terms)]
              (simplify (rest vars) new-terms)))))
          
(defn eval-non-lamda-expr [term]
  (simplify '() (evaluate* (get-terms term))))

(defn evaluate* [terms]
  (if (empty? terms)
    '()
    (let [term (first terms)]
         (if (atomic? term)
           (cons term (evaluate* (rest terms)))
           (if (lambda? term)
             (apply-lambda term (rest terms))
             (cons (eval-non-lamda-expr term)
                   (evaluate* (rest terms))))))))
    
(defn evaluate [term]
  (if (atomic? term)
    term
    (if (lambda? term)
      (first (apply-lambda term '()))
      (eval-non-lamda-expr term))))

(defn apply-lambda
  [lambda terms]
  (if (empty? terms)
    (cons (make-lambda (get-bound-vars lambda) (evaluate* (get-terms lambda))) '()) 
    (let [lambda* (evaluate (apply-var lambda (first terms)))]
           (if (and (not (atomic? lambda*)) (lambda? lambda*))
             (apply-lambda lambda* (rest terms))
             (cons lambda* (evaluate* (rest terms)))))))  
               
