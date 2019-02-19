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

import com.github.tototoshi.csv.DefaultCSVFormat
import com.github.tototoshi.csv.{CSVReader => TototoshiCSVReader}
import purecsv.safe.converter.defaults.string.Trimming

/**
  * A [[purecsv.unsafe.RecordSplitter]] that uses the scala-csv library for extracting records from a [[Reader]]
  */
object RecordSplitterImpl extends RecordSplitter[Reader] {
  private val EmptyString = ""

  override def getRecords(reader: Reader,
                          fieldSep: Char,
                          quoteCharacter: Char,
                          firstLineHeader: Boolean,
                          trimming: Trimming,
                          fields: Seq[String]): Iterator[Iterable[String]] = {
    implicit val csvFormat: DefaultCSVFormat = new DefaultCSVFormat {
      override val delimiter: Char = fieldSep
      override val quoteChar: Char = quoteCharacter
    }

    val csvReader = TototoshiCSVReader.open(reader)
    if (firstLineHeader) {
      toValuesIteratorWithHeadersOrdering(csvReader, trimming, fields)
    } else {
      toValuesIteratorWithoutHeadersOrdering(csvReader, trimming)
    }
  }

  private def toValuesIteratorWithHeadersOrdering(csvReader: TototoshiCSVReader, trimming: Trimming, fields: Seq[String]) =
    csvReader.iteratorWithHeaders
      .map(line => line.mapValues(trimming.trim))
      .map(f => fields.map(field => f.getOrElse(field, EmptyString)))

  private def toValuesIteratorWithoutHeadersOrdering(csvReader: TototoshiCSVReader, trimming: Trimming) =
    csvReader.iterator
      .map(line => line.map(trimming.trim))
      .filter(array => array.size != 1 || array(0) != EmptyString)
}
