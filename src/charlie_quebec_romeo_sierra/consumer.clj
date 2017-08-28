(ns charlie-quebec-romeo-sierra.consumer
  (:require [kinsky.client :as client]
            [kinsky.async :as async]
            [clojure.core.async :refer [go-loop <! put!]] ))

(def consumers (atom {}))

(defprotocol ConsumerController
  (output-channel [this])
  (subscribe [this topic])
  (unsubscribe [this])
  (commit [this])
  (stop [this]))

(defrecord KafkaConsumerController
  [control output]
  ConsumerController
  (output-channel [this]
    output)
  (subscribe [this topic]
    (put! control {:op :subscribe :topic topic}))
  (unsubscribe [this]
    (put! control {:op :unsubscribe}))
  (commit [this]
    (put! control {:op :commit}))
  (stop [this]
    (put! control {:op :stop})))

(defn- register-consumer
  [type_of consumer]
  (swap! consumers assoc type_of consumer)
  consumer)

(defn- listen
  [type_of consumer handler]
  (let [[output control] consumer
        topic type_of]
    (go-loop
      []
      (when-let [record (<! output)]
        (when (= (:type record) :record)
          (handler record))
        (recur)))
    (put! control {:op :subscribe :topic topic})
    (put! control {:op :commit})))

(defn create-consumer
  [type_of handler]
  (listen type_of
          (register-consumer type_of
                             (async/consumer
                               {:bootstrap.servers "localhost:9092"
                                :group.id type_of}
                               (client/keyword-deserializer)
                               (client/edn-deserializer)))
          handler))

(def consumer (memoize create-consumer))
