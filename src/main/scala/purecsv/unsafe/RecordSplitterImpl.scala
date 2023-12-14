/* Copyright 2015 Mario Pastorelli (pastorelli.mario@gmail.com)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package purecsv.unsafe

import java.io.Reader

import com.github.tototoshi.csv.{DefaultCSVFormat, CSVReader => TototoshiCSVReader}
import purecsv.config.{Headers, Trimming}

/**
 * A [[purecsv.unsafe.RecordSplitter]] that uses the scala-csv library for extracting records from a [[Reader]]
 */
object RecordSplitterImpl extends RecordSplitter[Reader] {
  private val EmptyString = ""

  override def getRecords(reader: Reader,
                          fieldSep: Char,
                          quoteCharacter: Char,
                          headers: Headers,
                          trimming: Trimming,
                          fields: Seq[String],
                          headerMapping: Map[String, String]
  ): Iterator[Iterable[String]] = {
    implicit val csvFormat: DefaultCSVFormat = new DefaultCSVFormat {
      override val delimiter: Char = fieldSep
      override val quoteChar: Char = quoteCharacter
    }

    val reverseHeaders = headerMapping.map(_.swap)

    val csvReader = TototoshiCSVReader.open(reader)
    headers match {
      case Headers.ParseHeaders =>
        toValuesIteratorWithHeadersOrdering(csvReader, trimming, fields, reverseHeaders)
      case Headers.None =>
        toValuesIteratorWithoutHeadersOrdering(csvReader, trimming, 0)
      case Headers.ReadAndIgnore =>
        toValuesIteratorWithoutHeadersOrdering(csvReader, trimming, 1)
    }
  }

  private def toValuesIteratorWithHeadersOrdering(csvReader: TototoshiCSVReader,
                                                  trimming: Trimming,
                                                  fields: Seq[String],
                                                  headerMapping: Map[String, String]
  ) =
    csvReader.iteratorWithHeaders
      .map(line => line.view.mapValues(trimming.trim))
      .filter(array => array.size != 1 || array.head._2.trim != EmptyString)
      .map(f => fields.map(field => f.getOrElse(headerMapping.getOrElse(field, field), EmptyString)))

  private def toValuesIteratorWithoutHeadersOrdering(csvReader: TototoshiCSVReader,
                                                     trimming: Trimming,
                                                     linesToBeDropped: Int
  ) =
    csvReader.iterator
      .drop(linesToBeDropped)
      .map(line => line.map(trimming.trim))
      .filter(array => array.size != 1 || array.head.trim != EmptyString)
}
