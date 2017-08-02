package com.mm.marketgauge.service

trait DataService extends com.mm.marketgauge.util.LogHelper {
  
  private[service] val dataDownloader = DataDownloader.getDataDownloader()
  
}