package com.mm.marketgauge.util
import scala.util.control.Exception.allCatch


object Utilities {
  
  def getDouble(doubleStr:String):Double = 
    allCatch opt doubleStr.toDouble match {
    case Some(doubleNum) => doubleNum
    case _ => Double.NaN
  }
  
  
}