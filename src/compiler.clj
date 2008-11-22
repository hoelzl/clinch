;;; Definition of the compiler structure.

(import ['clojure.asm])
;; (import ['clojure.lang])

(defn get-type [class-name]
  (.getType clojure.asm.Type (class class-name)))

(def $max-positional-arity 20)
(def $object-type (get-type Object))
(def $keyword-type (get-type clojure.lang.Keyword))
(def $var-type (get-type clojure.lang.Var))
(def $symbol-type (get-type clojure.lang.Symbol))
(def $ifn-type (get-type clojure.lang.IFn))
(def $rt-type (get-type clojure.lang.RT))
(def $class-type (get-type Class))
(def $reflector-type (get-type clojure.lang.Reflector))
(def $throwable-type (get-type Throwable))
(def $boolean-object-type (get-type Boolean))
(def $i-persistent-map-type (get-type clojure.lang.IPersistentMap))

(def $arg-types) ;; Type array...
(def $exception-types) ;; Type array...

(defstruct compiler
  :local-env :loop-locals :loop-label :constants :keywords :vars
  :method :in-catch-finally 
  :source :source-path :compile-path :compile-files
  :line :line-before :line-after
  :next-local-num :ret-local-num
  :loader)