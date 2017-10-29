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
package purecsv

import java.io.{File, PrintWriter}

import purecsv.unsafe.RecordSplitter.defaultFieldSeparatorStr
import purecsv.unsafe.converter.Converter


package object csviterable {


  implicit class CSVRecord[A,R <: Converter[A,Seq[String]]](a: A)(implicit rfc: R) {
    def toCSV(sep: String = defaultFieldSeparatorStr): String =
      rfc.to(a).mkString(sep)
  }

  /**
   * Helper class that adds methods for converting and writing an Iterable of [[A]] into CSV format.
   *
   * @param iter
   * @param rfc The implicit class that allows converting [[A]] to a [[Seq]]
   * @tparam A The type that can be converted to a CSV record
   * @tparam R The type of the converter [[A]] <-> [[Seq]]
   */
  implicit class CSVIterable[A,R <: Converter[A,Seq[String]]](iter: Iterable[A])(implicit rfc: R) {

    /** Convert all the values in [[iter]] into CSV lines */
    def toCSVLines(sep: String = defaultFieldSeparatorStr): Iterable[String] =
      iter.map(a => rfc.to(a).mkString(sep))

    /** Convert the values in [[iter]] into a CSV string */
    def toCSV(sep: String = defaultFieldSeparatorStr): String = toCSVLines(sep).mkString(System.lineSeparator())

    /**
     * Convert [[iter]] to CSV lines and then write them into the [[PrintWriter]]
     *
     * @param writer Where to write the CSV lines
     * @param sep The CSV separator
     * @param header An optional header. If it is set, then it is printed as first line
     */
    def writeCSVTo(writer: PrintWriter, sep: String, header: Option[Seq[String]]): Unit = {
      header.foreach(h => writer.println(h.mkString(sep)))
      toCSVLines(sep).foreach(writer.println)
    }

    /** @see [[writeCSVTo]] */
    def writeCSVToFile(file: File, sep: String = defaultFieldSeparatorStr, header: Option[Seq[String]] = None): Unit = {
      val writer = new PrintWriter(file)
      this.writeCSVTo(writer, sep, header)
      writer.close()
    }

    /** @see [[writeCSVToFile(File,String,Option[Seq[String]]):Unit*]] */
    def writeCSVToFileName(fileName: String, sep: String = defaultFieldSeparatorStr, header: Option[Seq[String]] = None): Unit = {
      writeCSVToFile(new File(fileName), sep, header)
    }
  }
}
