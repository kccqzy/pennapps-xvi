(ns pennapps-xvi.core
    (:require
     [clojure.core]
      [reagent.core :as r]))


;; -------------------------
;; Views

(def dummy-portfolio-data
  [
   ["U.S. Stocks" "11"]
   ["U.S. Bonds" "22"]
   ["Cryptocurrencies" "19"]
   ["Energy" "40"]
   ["International Stock" "8"]
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

(defn overview []
  (r/create-class
   {:component-did-mount
    (fn [this]
      (js/c3.generate (clj->js {"bindto" "#portfolio-composition"
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
  [:h2 "Short-Term Trading"])

(defn analytics []
  [:h2 "Analytics"])

(def tabmap
  {"Overview" overview
   "Long-Term Investments" investments
   "Short-Term Trading" trading
   "Analytics" analytics})

(def tabs
  (keys tabmap))

(defonce current-tab (r/atom "Overview"))

(defn body []
  [:div {:id "body"}
   [:div#sidebar
    [:ul
     (map (fn [t] [:li {:key t} [:a {:on-click #(swap! current-tab (fn [_] t))
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
      [:p [:strong "Important Disclaimer: "] "Any past performance, projection, forecast or simulation of results is not necessarily indicative of the future or likely performance of any company or investment. Investors are responsible for paying all taxes imposed by relevant authorities on any interests received and the amount of tax payable is dependent on individual circumstances. The information provided does not take into account your specific investment objectives, financial situation or particular needs. Before you act on any information on this site, you should always seek independent financial, tax, and legal advice or make such independent investigations as you consider necessary or appropriate regarding the suitability of the investment product, taking into account your specific investment objectives, financial situation or particular needs."]]]
  )

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
