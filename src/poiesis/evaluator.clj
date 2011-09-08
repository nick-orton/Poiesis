(ns poiesis.evaluator
  (:use poiesis.forms))


(declare replace-free evaluate apply-lambda)

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

(defn apply-var
  [lambda arg]
  (let [vars (get-bound-vars lambda)
        terms (get-terms lambda)]
       (if (empty? vars)
         lambda
         (let [variable (first vars)
               new-terms (beta-reduce variable arg terms)]
              (make-lambda (rest vars) new-terms)))))
           
(defn evaluate* [terms]
  (if (empty? terms)
    '()
    (let [term (first terms)]
         (if (lambda? term)
           (apply-lambda term (rest terms))
           (cons (evaluate term) (evaluate* (rest terms)))))))
    
(defn evaluate [term]
  (if (atomic? term)
    term
    (make-lambda '() (evaluate* (get-terms term)))))

(defn apply-lambda
  [lambda terms]
  (if (empty? terms)
      lambda
      (let [term (first terms)
            lambda* (evaluate (apply-var lambda term))]
           (if (lambda? lambda*)
               (apply-lambda lambda* (rest terms))
               (cons lambda* (evaluate* (rest terms)))))))  
               
