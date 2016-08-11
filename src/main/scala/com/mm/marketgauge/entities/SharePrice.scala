package com.mm.marketgauge.entities

case class SharePrice(ticker:String, price:Double, asOfDate:java.util.Date,
                 currentEps:Double, forwardEps:Double,
                 movingAverage:Double, exDivDate:String,
                 peg:Double, shortRatio:Double)
                 
                 
                