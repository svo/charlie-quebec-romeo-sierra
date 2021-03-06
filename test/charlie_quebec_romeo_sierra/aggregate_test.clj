(ns charlie-quebec-romeo-sierra.aggregate-test
  (:require [charlie-quebec-romeo-sierra.aggregate
             :as aggregate
             :refer :all]
            [midje.open-protocols :refer [defrecord-openly]])
  (:use [midje.sweet :only [facts fact => provided unfinished]]))

(unfinished factory)

(facts

  (defrecord-openly TestAggregate []
    aggregate/Aggregate
    (data [this] ..data..)
    (identifier [this] ..identifier..)
    (type-of [this] ..type..)
    (valid? [this event] ..result..)
    (process [this event] ..result..))

  (defrecord-openly TestAggregateFactory []
    aggregate/AggregateFactory
    (create [this type_of identifier] (->TestAggregate)))

  (fact
    "should have data"
    (data (->TestAggregate)) => ..data..)

  (fact
    "should have identifier"
    (identifier (->TestAggregate)) => ..identifier..)

  (fact
    "should have type"
    (type-of (->TestAggregate)) => ..type..)

  (fact
    "should determine if event is valid for aggregate"
    (valid? (->TestAggregate) ..event..) => ..result..)

  (fact
    "should apply event to aggregate"
    (process (->TestAggregate) ..event..) => ..result..)

  (fact
    "should register aggregate"
    (let [aggregate_constructor ->TestAggregate]
      (aggregate/register-aggregate ..type_of.. aggregate_constructor)
      @aggregate/aggregates => {..type_of.. aggregate_constructor}))

  (facts
    "with aggregates"
    (with-redefs [aggregate/aggregates
                  (atom {..type_of.. (->TestAggregateFactory)})]

    (fact
      "should create aggregate"
        (aggregate/get-aggregate ..type_of..
                                 ..identifier..) => (->TestAggregate)))

    (fact
      "should clear aggregates"
      (aggregate/clear)
      @aggregate/aggregates => {})))
