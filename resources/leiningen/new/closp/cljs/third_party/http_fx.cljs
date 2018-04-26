;; Taken from https://github.com/Day8/re-frame-http-fx until it supports default response-format


(ns {{ns}}.third-party.http-fx
  (:require [goog.net.ErrorCode :as errors]
            [re-frame.core :refer [reg-fx dispatch console]]
            [ajax.core :as ajax]))

;; I provide the :http-xhrio effect handler leveraging cljs-ajax lib
;; see API docs https://github.com/JulianBirch/cljs-ajax
;; Note we use the ajax-request.
;;
;; Deviation from cljs-ajax options in request
;; :handler       - not supported, see :on-success and :on-failure
;; :on-success    - event vector dispatched with result
;; :on-failure    - event vector dispatched with result
;;
;; NOTE: if you need tokens or other values for your handlers,
;;       provide them in the on-success and on-failure event e.g.
;;       [:success-event "my-token"] your handler will get event-v
;;       [:success-event "my-token" result]


(defn ajax-xhrio-handler
  "ajax-request only provides a single handler for success and errors"
  [on-success on-failure xhrio [success? response]]
  ; see http://docs.closure-library.googlecode.com/git/class_goog_net_XhrIo.html
  (if success?
    (on-success response)
    (let [details (merge
                    {:uri             (.getLastUri xhrio)
                     :last-method     (.-lastMethod_ xhrio)
                     :last-error      (.getLastError xhrio)
                     :last-error-code (.getLastErrorCode xhrio)
                     :debug-message   (-> xhrio .getLastErrorCode (errors/getDebugMessage))}
                    response)]
      (on-failure details))))


(defn request->xhrio-options
  [{:as   request
    :keys [on-success on-failure]
    :or   {on-success      [:http-no-on-success]
           on-failure      [:http-no-on-failure]}}]
  ; wrap events in cljs-ajax callback
  (let [api (new js/goog.net.XhrIo)]
    (-> request
        (assoc
          :api     api
          :handler (partial ajax-xhrio-handler
                            #(dispatch (conj on-success %))
                            #(dispatch (conj on-failure %))
                            api))
        (dissoc :on-success :on-failure))))

(defn http-effect
  [request]
  (let [seq-request-maps (if (sequential? request) request [request])]
    (doseq [request seq-request-maps]
      (-> (assoc request :response-format (ajax/transit-response-format))
          request->xhrio-options ajax/ajax-request))))

(reg-fx :http-xhrio http-effect)
