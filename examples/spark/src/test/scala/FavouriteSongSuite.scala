import org.apache.spark.SparkContext
import org.joda.time.Period
import org.scalatest.{Matchers, FunSuite}
import purecsv.example.favouritesong.FavouriteSong
import purecsv.example.favouritesong.FavouriteSong.periodStringConverter
import purecsv.safe.converter.StringConverter


class FavouriteSongSuite extends FunSuite with Matchers {

  test("JodaTime Period can be read from String") {
    val period = StringConverter[Period].from("P0000-00-00T00:04:53")
    period.getYears   should be (0)
    period.getMonths  should be (0)
    period.getDays    should be (0)
    period.getHours   should be (0)
    period.getMinutes should be (4)
    period.getSeconds should be (53)
  }

  test("FavouriteSong returns the songs with highest " +
       "like per artist and the number of broken records") {

    // 3 records with 1 broken (Human Nature)
    val rawRecords = Seq(
        "Billie Jean,Michael Jackson,Thriller,P0000-00-00T00:04:53,6430000"
      , "Human Nature,Michael Jackson,Thriller,P012,"
      , "Thriller,Michael Jackson,Thriller,P0000-00-00T00:05:59,5700000"
      )

    val sc = new SparkContext("local[2]", "test favourite song")
    val rawSongs = sc.parallelize(rawRecords)
    val result = FavouriteSong(rawSongs)

    // one record is broken
    result.brokenRecords should be (1)

    // the top song for Michael Jackson is Billie Jean
    result.artistToBestSong should contain theSameElementsAs(Seq("Michael Jackson" -> "Billie Jean"))
  }

}
