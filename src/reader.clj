;;; Implementation of the Clojure reader in Clojure.

(ns clojure.reader
  (:refer-clojure :exclude [read-string])
  (:import (java.io)
	   (java.util.regex Pattern Matcher)
	   (java.util ArrayList List Map)
	   (java.math BigInteger BigDecimal)))

(in-ns 'clojure.reader)


(import '(clojure.lang IFn))

(def $symbol-pattern #"[:]?([\\D&&[^/]].*/)?([\\D&&[^/]][^/]*)")
(def $int-pattern #"([-+]?)(?:(0)|([1-9][0-9]*)|0[xX]([0-9A-Fa-f]+)|0([0-7]+)|([1-9][0-9]?)[rR]([0-9A-Za-z]+)|0[0-9]+)\\.?")
(def $ratio-pattern #"([-+]?[0-9]+)/([0-9]+)")
(def $float-pattern #"[-+]?[0-9]+(\\.[0-9]+)?([eE][-+]?[0-9]+)?[M]?")
(def $slash '/)
(def $clojure-slash `/)

(defn read-string
  "Default reader function for double quote."
  [])

(defn read-comment
  "Default reader function for semicolon.
Also default dispatch reader function for exclamation mark."
  [])

(defn make-wrapping-reader
  "Returns a reader that wraps its result with 'wrapper'."
  [wrapper]
  wrapper)

(defn read-syntax-quote
  "Default reader function for backquote."
  [])

(defn read-unquote
  "Default reader function for tilde."
  [])

(defn read-list
  "Default reader for open paren."
  [])

(defn read-unmatched-delimiter
  "Default reader function for close paren, bracket and brace."
  [])

(defn read-vector
  "Default reader function for open bracket."
  [])

(defn read-map
  "Default reader function for open brace."
  [])

(defn read-character
  "Default reader function for backslash."
  [])

(defn read-arg
  "Default reader function for per-cent sign."
  [])

(defn read-dispatch
  "Default reader function for hash."
  [])

(def $macros (ref {\" read-string
		   \; read-comment
		   \' (make-wrapping-reader 'quote)
		   \@ (make-wrapping-reader 'deref)
		   \^ (make-wrapping-reader 'meta)
		   \` read-syntax-quote
		   \~ read-unquote
		   \( read-list
		   \) read-unmatched-delimiter
		   \[ read-vector
		   \] read-unmatched-delimiter
		   \{ read-map
		   \} read-unmatched-delimiter
		   \\ read-character
		   \% read-arg
		   \# read-dispatch}))

(defn read-meta
  "Default reader dispatch function for caret."
  [])

(defn read-var
  "Default reader dispatch function for single quote."
  [])

(defn read-regex
  "Default reader dispatch function for double quote."  
  [])

(defn read-fn
  "Default reader dispatch function for open paren."
  [])

(defn read-set
  "Default reader dispatch function for open brace."
  [])

(defn read-eval
  "Default reader dispatch function for equals."
  [])

(defn read-unreadable
  "Default reader dispatch function for less-than sign."
  [])

(def $dispatch-macros (ref {\^ read-meta
			    \' read-var
			    \" read-regex
			    \( read-fn
			    \{ read-set
			    \= read-eval
			    \! read-comment
			    \< read-unreadable}))
