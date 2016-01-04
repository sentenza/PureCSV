package purecsv.unsafe

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
import java.io.Reader


/**
 * A [[purecsv.unsafe.RecordSplitter]] that uses the OpenCSV library for extracting records from a [[Reader]]
 */
object OpenCSVSplitter extends RecordSplitter[Reader] {

  override def getRecords(reader: Reader, fieldSep: Char, quoteChar: Char, firstLine: Int): Iterator[Seq[String]] = {
    val csvReader = new au.com.bytecode.opencsv.CSVReader(reader, fieldSep, quoteChar, firstLine)

    new Iterator[Seq[String]] {
      private var nextRecord = {
        val r = csvReader.readNext()
        if (r == null) {
          csvReader.close()
        }
        r
      }

      def hasNext(): Boolean = (nextRecord != null)
      def next(): Seq[String] = {
        if (nextRecord == null) {
          throw new NoSuchElementException()
        } else {
          val nextRecordBuf = this.nextRecord
          this.nextRecord = csvReader.readNext()
          if (!this.hasNext()) {
            csvReader.close()
          }
          nextRecordBuf
        }
      }
    }
  }
}