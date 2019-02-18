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

import purecsv.safe.converter.defaults.string.Trimming


/**
 * A [[purecsv.unsafe.RecordSplitter]] that uses the OpenCSV library for extracting records from a [[Reader]]
 */
object RecordSplitterImpl extends RecordSplitter[Reader] {

  override def getRecords(reader: Reader,
                          fieldSep: Char,
                          quoteChar: Char,
                          firstLine: Int,
                          trimming: Trimming): Iterator[Array[String]] = {
    val csvReader = new com.github.marklister.collections.io.CSVReader(reader, fieldSep, quoteChar, firstLine)
    val mappedReader = csvReader.map(line => line.map(trimming.trim(_)))
    mappedReader.filter(array => array.size != 1 || array(0) != "") // skip empty lines
  }
}
