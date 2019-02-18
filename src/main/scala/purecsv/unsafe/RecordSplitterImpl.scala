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
import purecsv.safe.converter.defaults.string.Trimming


/**
 * A [[purecsv.unsafe.RecordSplitter]] that uses the scala-csv library for extracting records from a [[Reader]]
 */
object RecordSplitterImpl extends RecordSplitter[Reader] {

  override def getRecords(reader: Reader,
                          fieldSep: Char,
                          quoteCharacter: Char,
                          firstLineHeader: Boolean,
                          trimming: Trimming): Iterator[Iterable[String]] = {

    implicit val csvFormat = new DefaultCSVFormat {
      override val delimiter: Char = fieldSep
      override val quoteChar: Char = quoteCharacter
    }
    val csvReader = com.github.tototoshi.csv.CSVReader.open(reader)
    val mappedReader = csvReader.iterator.map(line => line.map(trimming.trim(_)))
    val filtered = mappedReader.filter(array => array.size != 1 || array(0) != "") // skip empty lines
    if (firstLineHeader) {
      filtered.drop(1)
    } else {
      filtered
    }
  }
}
