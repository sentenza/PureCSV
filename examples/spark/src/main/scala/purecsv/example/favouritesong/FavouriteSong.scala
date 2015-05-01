package purecsv.example.favouritesong

import au.com.bytecode.opencsv.CSVParser
import org.apache.spark.Accumulator
import org.apache.spark.AccumulatorParam.LongAccumulatorParam
import org.apache.spark.rdd.RDD
import org.joda.time.Period
import org.joda.time.format.ISOPeriodFormat
import purecsv.safe._
import purecsv.safe.converter.{RawFieldsConverter, StringConverter}

import scala.util.{Failure, Success, Try}


case class Song(title: String, artist: String, album: String, length: Period, likes: Int)

/**
 * The result of the function [[FavouriteSong]] contains the result itself
 * and the number of broken records
 */
case class FavouriteSongResult(artistToBestSong: Seq[(String,String)], brokenRecords: Long)


/** The function that extract from an RDD of songs in CSV format the favourite song for each artist */
object FavouriteSong extends (RDD[String] => FavouriteSongResult) {

  /* Period doesn't have a StringConverter, we provide one here */
  implicit val periodStringConverter = new StringConverter[Period] {
    override def tryFrom(str: String): Try[Period] = {
      Try(ISOPeriodFormat.alternateExtended().parsePeriod(str))
    }

    override def to(period: Period): String = ISOPeriodFormat.alternateExtended().print(period)
  }

  @transient lazy val csvParser = new CSVParser()

  override def apply(rawSongs: RDD[String]): FavouriteSongResult = {

    // accumulators for errors while parsing
    val brokenSongsAcc = new Accumulator[Long](0l, LongAccumulatorParam, Some("Broken song records"))

    // read songs from CSV lines avoiding broken lines
    val songs = rawSongs.flatMap(csv => tryParseSong(brokenSongsAcc, csv))

    // get the top song for each artist artist
    val mostLikedSongPerArtist = songs.groupBy(_.artist).mapValues(_.maxBy(_.likes).title).collect()

    FavouriteSongResult(mostLikedSongPerArtist, brokenSongsAcc.value)
  }

  /**
   * Try to parse the "raw song"
   *
   * @param brokenAcc the accumulator to increment if a broken record has been found
   * @param str the raw song to parse
   * @return the [[Song]] inside [[Some]] if the record has been parsed successfully else [[None]]
   */
  def tryParseSong(brokenAcc: Accumulator[Long], str: String): Option[Song] = {
    // we try to parse the record raw events and then, if successful, we try to convert
    // the raw fields to a Song instance
    val errorOrSong = Try(csvParser.parseLine(str)).flatMap(rawFields => RawFieldsConverter[Song].tryFrom(rawFields))

    errorOrSong match {
      case Success(song) => Some(song)
      case Failure(error) => { brokenAcc += 1; None }
    }
  }

}
