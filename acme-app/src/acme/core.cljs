(ns acme.core
  (:require [x11]))

(defn handler [^js client wid disp]
  (let [gc (.AllocID client)
        _ (.CreateGC client gc wid)
        ;; .-white_pixel & .-black_pixel syntactic sugar don't
        ;; work with advanced compilation for some reason
        white (-> disp .-screen first js->clj (get "white_pixel"))
        black (-> disp .-screen first js->clj (get "black_pixel"))
        cid-white (.AllocID client)
        cid-black (.AllocID client)
        _ (.CreateGC client cid-white wid (clj->js {:foreground white :background black}))
        _ (.CreateGC client cid-black wid (clj->js {:foreground black :background white}))]
    (fn [ev]
      (when (== (.-type ev) 12)
        (.PolyFillRectangle
         client wid cid-black
         (clj->js [0 0 500 500]))
        (.PolyText8
         client wid cid-white
         50 50
         (clj->js ["Hello, Shadow-Cljs!"]))))))

(defn error-handler [e]
  (println e))

(defn add-handlers! [^js client wid disp]
  (.on client "event" (handler client wid disp))
  (.on client "error" error-handler))

(defn window! [disp]
  (let [exposure (.-Exposure x11/eventMask)
        pointer-motion (.-PointerMotion x11/eventMask)
        client ^js (.-client disp)
        root (-> disp .-screen first .-root)
        wid (.AllocID client)]
    (.CreateWindow
     client
     wid root
     0 0 500 500
     0 0 0 0
     (clj->js {:eventMask (bit-or exposure pointer-motion)}))
    (.MapWindow client wid)
    (add-handlers! client wid disp)))

(defn client! []
  (x11/createClient
   (fn [err disp]
     (if err
       (println err)
       (window! disp)))))

(defn main! []
  (client!))
