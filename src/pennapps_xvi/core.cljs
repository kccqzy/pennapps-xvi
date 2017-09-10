(ns pennapps-xvi.core
    (:require
     [clojure.core]
      [reagent.core :as r]))

(defn floor-factor [n f]
  (* f (quot n f)))

(defn gen-dummy-stock-data
  [len]
  (let [rand-range 0.8
        rand-range-half (/ rand-range 2)]
    (take
     len
     (iterate (fn [{:strs [open high low close volume date]}]
                {"open" close
                 "high" (* close (+ 1.0 (* (- (rand rand-range) rand-range-half) (- (rand rand-range) rand-range-half))))
                 "low" (* close (+ 1.0 (* (- (rand rand-range) rand-range-half) (- (rand rand-range) rand-range-half))))
                 "close" (* close (+ 1.001 (* (- (rand rand-range) rand-range-half) (- (rand rand-range) rand-range-half))))
                 "volume" (* volume (+ 1.0 (* (- (rand rand-range) rand-range-half) (- (rand rand-range) rand-range-half) (- (rand rand-range) rand-range-half))))
                 "date" (js/Date. (+ (.getTime date) 60000))})
              {"date" (js/Date. (- (floor-factor (js/Date.now) 60000) (* 60000 len)))
               "open" 62.40
               "high" 63.34
               "low" 61.79
               "close" 62.88
               "volume" 37617413}))))


;; -------------------------
;; Views

(def dummy-portfolio-data
  [
   ["U.S. Stocks" "1123"]
   ["U.S. Bonds" "2287"]
   ["Cryptocurrencies" "3419"]
   ["Energy" "4200"]
   ["International Stock" "877"]
   ["Emerging Markets" "1877"]
   ])

(def friend-list
  [
   ["1" "Jennifer" "AAPL"]
   ["2" "Jake" "GOOG"]
   ["3" "Mike" "AMZN"]
   ["4" "Sam" "MSFT"]
   ["5" "Tim" "VTSMX"]
   ["6" "Julie" "F"]
   ["7" "Harry" "SYMC"]
   ["8" "Jolene" "XOM"]
   ["9" "William" "BLK"]
   ["10" "Elaina" "BLK"]
   ["11" "Shu" "CSCO"]
   ["12" "Irene" "FDX"]
   ["13" "Kelly" "FB"]])

(def chart-colors
  ["#A96186" "#A96162" "#A98461" "#A9A861" "#86A961" "#589857" "#4E876A" "#4E8786" "#4E6B87" "#4E4E87" "#6A4E87" "#8F5390"])

(defn overview []
  (r/create-class
   {:component-did-mount
    (fn [this]
      (js/c3.generate (clj->js {"bindto" "#portfolio-composition"
                                "color" {"pattern"
                                         (clojure.core/shuffle chart-colors)}
                                "data" {"columns" dummy-portfolio-data
                                        "type" "donut"}}))
      )
    :reagent-render
    (fn [_] [:div
             [:h2 "Overview"]
             [:h3 "Your Investment Portfolio"]
             [:div#portfolio-composition]
             [:h3 "Friends\u2019 Picks"]
             [:div#stock-picks
              (map (fn [[id name pick]]
                     [:div.stock-pick {:key name}
                      [:div [:img {:src (str "img/profile/pic-" id ".jpg")}]]
                      [:div.name name]
                      [:div.pick pick]])
                   (take 5 (clojure.core/shuffle friend-list)))]
             ])}))

(defn investments []
  [:h2 "Long-Term Investments"])

(defn trading []
  (r/create-class
   {:component-did-mount
    (fn [this]
      (let [raw-data (gen-dummy-stock-data 100)
            width 890
            height 450
            x (-> js/techan.scale (.financetime) (.range (clj->js [0 width])))
            y (-> js/d3 (.scaleLinear) (.range (clj->js [height 0])))
            candlestick (-> js/techan.plot (.candlestick) (.xScale x) (.yScale y))
            xAxis (-> js/d3 (.axisBottom) (.scale x))
            yAxis (-> js/d3 (.axisLeft) (.scale y))
            svg (-> js/d3 (.select "div#candlestick") (.append "svg") (.attr "width" 960) (.attr "height" 500) (.append "g")
                    (.attr "transform" "translate(50,20)"))
            accessor (-> candlestick (.accessor))
            data (-> (clj->js raw-data) (.sort (fn [a b] (js/d3.ascending (.d accessor a) (.d accessor b)))))
            ]
        (js/console.log data)
        (-> svg (.append "g") (.attr "class" "candlestick"))
        (-> svg (.append "g") (.attr "class" "x axis") (.attr "transform" "translate(0, 450)"))
        (-> svg (.append "g") (.attr "class" "y axis") (.append "text") (.attr "transform" "rotate(-90)") (.attr "y" "6")
            (.attr "dy" ".71em") (.style "text-anchor" "end") (.text "Price ($)"))
        (.domain x (.map data (.-d (-> candlestick (.accessor)))))
        (.domain y (.domain (js/techan.scale.plot.ohlc data (-> candlestick (.accessor)))))
        (-> svg (.selectAll "g.candlestick") (.datum data) (.call candlestick))
        (-> svg (.selectAll "g.x.axis") (.call xAxis))
        (-> svg (.selectAll "g.y.axis") (.call yAxis))
        ))
    :reagent-render
    (fn [_]
      [:h2 "Short-Term Trading"]
      [:div#candlestick])}))

(defn analytics []
  [:h2 "Analytics"])

(def tabmap
  {"Overview" overview
   "Long-Term Investments" investments
   "Short-Term Trading" trading
   "Analytics" analytics})

(def tabs
  (keys tabmap))

(defonce current-tab (r/atom (or (-> js/window.localStorage (.getItem "current-tab")) "Overview")))

(defn body []
  [:div {:id "body"}
   [:div#sidebar
    [:ul
     (map (fn [t] [:li {:key t} [:a {:on-click #(do
                                                  (-> js/window.localStorage (.setItem "current-tab" t))
                                                  (swap! current-tab (fn [_] t)))
                                     :class (if (= @current-tab t) "selected" "")} t]]) tabs)]]
   [:div#main-content
    [(get tabmap @current-tab "Overview")]]])

(defn home-page []
      [:div#container
     [:div#header
      [:img#logo {:src "img/logo.png" :height 64 :width 65}]
      [:p#title "Stock Advisors"]
      [:p#menu "Welcome Johnson! "
       [:button "Logout"]]]
     [body]
     [:div#footer
      [:p [:strong "Copyright (c) 2017 Stock Advisors and Company. All rights reserved."]]
      [:p [:strong "Important Disclaimer: "] "Any past performance, projection, forecast or simulation of results is not necessarily indicative of the future or likely performance of any company or investment. The information provided does not take into account your specific investment objectives, financial situation or particular needs. Before you act on any information on this site, you should always seek independent financial, tax, and legal advice or make such independent investigations as you consider necessary or appropriate regarding the suitability of the investment product, taking into account your specific investment objectives, financial situation or particular needs."]]]
  )

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
