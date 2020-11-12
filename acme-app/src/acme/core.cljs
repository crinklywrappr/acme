(ns acme.core
  (:require [x11]))

(defn handler [^x11.XClient x wid disp]
  (let [gc (.AllocID x)
        _ (.CreateGC x gc wid)
        white (-> disp .-screen first .-white_pixel)
        black (-> disp .-screen first .-black_pixel)
        cid-white (.AllocID x)
        cid-black (.AllocID x)
        _ (.CreateGC x cid-white wid (clj->js {:foreground white :background black}))
        _ (.CreateGC x cid-black wid (clj->js {:foreground black :background white}))]
    (fn [ev]
      (when (== (.-type ev) 12)
        (.PolyFillRectangle
         x wid cid-black
         (clj->js [0 0 500 500]))
        (.PolyText8
         x wid cid-white
         50 50
         (clj->js ["Hello, Shadow-Cljs!"]))))))

(defn error-handler [e]
  (println e))

(defn add-handlers! [^x11.XClient x wid disp]
  (.on x "event" (handler x wid disp))
  (.on x "error" error-handler))

(defn window! [disp]
  (let [exposure (.-Exposure x11/eventMask)
        pointer-motion (.-PointerMotion x11/eventMask)
        x (.-client disp)
        root (-> disp .-screen first .-root)
        wid (.AllocID ^x11.XClient x)]
    (.CreateWindow
     ^x11.XClient x
     wid root
     0 0 500 500
     0 0 0 0
     (clj->js {:eventMask (bit-or exposure pointer-motion)}))
    (.MapWindow ^x11.XClient x wid)
    (add-handlers! x wid disp)))

(defn client! []
  (x11/createClient
   (fn [err disp]
     (if err
       (println err)
       (window! disp)))))

(defn main! []
  (client!))
