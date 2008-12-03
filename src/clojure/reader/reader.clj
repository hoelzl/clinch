;;; Implementation of the Clojure reader in Clojure.

(ns clojure.reader
  (:refer-clojure :exclude [read read-string])
  (:import (java.io)
	   (java.util.regex Pattern Matcher)
	   (java.util ArrayList List Map)
	   (java.math BigInteger BigDecimal)
	   (clojure.lang IFn
			 LineNumberingPushbackReader 
			 Numbers
			 RT)))

(in-ns 'clojure.reader)


(def $symbol-pattern 
     #"[:]?([\D&&[^/]].*/)?([\D&&[^/]][^/]*)")
(def $int-pattern 
     #"([-+]?)(?:(0)|([1-9][0-9]*)|0[xX]([0-9A-Fa-f]+)|0([0-7]+)|([1-9][0-9]?)[rR]([0-9A-Za-z]+)|0[0-9]+)\.?")
(def $ratio-pattern 
     #"([-+]?[0-9]+)/([0-9]+)")
(def $float-pattern 
     #"[-+]?[0-9]+(\.[0-9]+)?([eE][-+]?[0-9]+)?[M]?")
(def $slash '/)
(def $clojure-slash `/)

(def $macros {})

(defn macro? [char]
  (not (not ($macros char))))

(defn get-macro [char]
  ($macros char))

(defn clojure-whitespace? [char]
  (or (. Character (isWhitespace char))
      (= (int char) (int \,))))

(defn unread [reader char]
  (if (not (= char -1))
    (. reader (unread char))))

;;; Implementation of ReaderException in separate source file.

(defn match-number [string]
  (let [match (re-find $int-pattern string)]
    (if match
      (if (not (nil? (match 2)))
	0
	(let [negate? (= (match 1) "-")
	      [number radix] (cond (not (nil? (match 3)))
				   [(match 3) 10]
				   (not (nil? (match 4)))
				   [(match 4) 16]
				   (not (nil? (match 5)))
				   [(match 5) 8]
				   (not (nil? (match 7)))
				   [(match 7) (. Integer (parseInt (match 6)))])]
	  (if (nil? number)
	    nil
	    (let [n (BigInteger. number radix)
		  m (if negate? (. n negate) n)]
	      (. Numbers (reduce m))))))
      ;; TODO: implement floating-point and rational matchers
      )))

(defn read-number [reader initial-char]
  (let [sb (StringBuilder.)]
    (. sb (append initial-char))
    (loop [c (. reader read)]
      (if (or (= -1 c) (clojure-whitespace? c) (macro? c))
	(unread reader c)
	(let [c (char c)]
	  (. sb (append c))
	  (recur (. reader read)))))
    (let [string (. sb toString)
	  number (match-number string)]
      (if (nil? number)
	(throw (NumberFormatException. 
		(str "Invalid number: " string)))
	number))))


(defn read-token [])
(defn interpret-token [])

(defn read-unprotected [reader eof-is-error? eof-value recursive?]
  (loop [char (. reader read)]
    (cond (clojure-whitespace? char)
	  (recur (. reader read))
	  
	  (= -1 char)
	  (if eof-is-error?
	    (throw (Exception. "EOF while reading."))
	    eof-value)

	  (. Character (isDigit char))
	  (let [number (read-number reader char)]
	    (if (. RT (suppressRead))
	      nil
	      number))

	  (macro? char)
	  (let [result (get-macro char)]
	    (cond (. RT (suppressRead))
		  nil
		  
		  ;; No-op macros return the reader
		  (identical? result reader)
		  (recur (. reader read))

		  true
		  result))

	  (or (= char \+) (= char \-))
	  (let [char2 (. reader read)]
	    (if (. Character (isDigit char))
	      (do
		(unread reader char2)
		(let [number (read-number reader char)]
		  (if (. RT (suppressRead))
		    nil
		    number)))
	      (do
		(unread reader char2)
		(recur (. reader read)))))

	  true
	  (let [token (read-token reader char)]
	    (if (. RT (suppressRead))
	      nil
	      (interpret-token token))))))


(defn read [reader eof-is-error? eof-value recursive?]
  (try
   (read-unprotected reader eof-is-error? eof-value recursive?)
   (catch Exception e
     (if (or recursive?
	     (not (instance? LineNumberingPushbackReader reader)))
       (throw e)
       (throw (clojure.reader.ReaderException. (. reader getLineNumber) e))))))


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

(def read-quote (make-wrapping-reader 'quote))
(def read-deref (make-wrapping-reader 'deref))
(def read-meta (make-wrapping-reader 'meta))

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
		   \' read-quote
		   \@ read-deref
		   \^ read-meta
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

(defn read-sharp-meta
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

(def $dispatch-macros (ref {\^ read-sharp-meta
			    \' read-var
			    \" read-regex
			    \( read-fn
			    \{ read-set
			    \= read-eval
			    \! read-comment
			    \< read-unreadable}))
