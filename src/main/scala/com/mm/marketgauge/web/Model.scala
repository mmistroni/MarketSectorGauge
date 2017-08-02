package com.mm.marketgauge.web

// domain model
final case class Item(name: String, id: Long)
final case class Order(items: List[Item])
case class SharePriceDummy(ticker:String, price:Double)

