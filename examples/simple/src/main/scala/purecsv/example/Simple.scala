import purecsv.safe._

case class Test(start: Long, end: Int)

class Interval(val start: Long, val end: Long)

object Simple extends App {
  println(Test(10,20).toCSV())
  println(new Interval(10,20).toCSV())
}
