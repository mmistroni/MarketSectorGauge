

object TestUrlDownloader extends App {
  
  import scala.io._
  
  val data = Source.fromURL("https://biz.yahoo.com/ic/ind_index_alpha.html")
  
  var i = 0
  for (line <- data.getLines()) {
    if (line.indexOf("https://biz.yahoo.com/ic/") > 0 ) {
      val htmlIdx = line.indexOf(".html")
      if (htmlIdx > 0) {
        val substr = line.substring(line.indexOf("https://biz.yahoo.com/ic/") + 25 ,
                   htmlIdx)
        
        val digits  =substr.filter(_.isDigit)
        if (digits.size > 0) {
          i += 1
          println(s"$substr - $i ")
        }
      }
    }
    
  }
  
}