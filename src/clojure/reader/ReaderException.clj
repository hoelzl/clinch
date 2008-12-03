(ns clojure.reader.ReaderException
  (:gen-class :extends Exception
	      :init init-reader-exception
	      :constructors {[int, Throwable] [Throwable]}
	      :state line))


