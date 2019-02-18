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

import purecsv.safe.converter.defaults.string.Trimming


object RecordSplitter {
  val defaultFieldSeparator = ','
  val defaultFieldSeparatorStr = defaultFieldSeparator.toString
  val defaultQuoteChar = '"'
  val defaultQuoteStr = defaultQuoteChar.toString
}

trait RecordSplitter[R] {

  /** Split the input [[R]] into records, where each record is a sequence of raw fields */
  def getRecords(r: R, fieldSep: Char, quoteChar: Char, firstLine: Int, trimming: Trimming): Iterator[Array[String]]

  /**
   * Like [[getRecords(R, Char, Char, Int):Iterator[Seq[String]]*]] but with all parameters except the first set
   * to defaults and first line set to 0
   */
  def getRecords(r: R,
                 fieldSep: Char = RecordSplitter.defaultFieldSeparator,
                 quoteChar: Char = RecordSplitter.defaultQuoteChar,
                 trimming: Trimming = Trimming.NoAction): Iterator[Array[String]] = {
    getRecords(r, fieldSep, quoteChar, 0, trimming)
  }

  /**
   * Like [[getRecords(R, Char, Char, Int):Iterator[Seq[String]]*]] but with all parameters except the first set
   * to defaults and first line set to 1 to skip the first line. Useful to skip headers.
   */
  def getRecordsSkipHeader(r: R,
                           fieldSep: Char = RecordSplitter.defaultFieldSeparator,
                           quoteChar: Char = RecordSplitter.defaultQuoteChar,
                           trimming: Trimming = Trimming.NoAction): Iterator[Array[String]] = {
    getRecords(r, fieldSep, quoteChar, 1, trimming)
  }
}
