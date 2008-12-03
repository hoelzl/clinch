;;; Definitions of the various expression types.

(defstruct expression :type)

(defmulti ct-eval :type)
(defmulti emit (fn :type %1

(defstruct def-expr :type)
(defstruct let-expr :type)
(defstruct recur-expr :type)
(defstruct if-expr :type)
(defstruct let-expr :type)
(defstruct do-expr :type)
(defstruct fn-expr :type)
(defstruct constant-expr :type)
(defstruct the-var-expr :type)
(defstruct dot-expr :type)
(defstruct try-expr :type)
(defstruct throw-expr :type)
(defstruct monitor-enter-expr :type)
(defstruct monitor-exit-expr :type)
(defstruct catch-expr :type)
(defstruct finally-expr :type)
(defstruct new-expr :type)
(defstruct amp-expr :type)
