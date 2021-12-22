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

import java.io._

import purecsv.unsafe.{RecordSplitter,RecordSplitterImpl}
import purecsv.safe.converter.{RawFieldsConverter, StringConverter}
import shapeless.{Generic, ::, HList, HNil}

import scala.util.Try


package object safe {

  // String
  implicit val boolc:   StringConverter[Boolean] = purecsv.safe.converter.defaults.string.boolc
  implicit val bytec:   StringConverter[Byte]    = purecsv.safe.converter.defaults.string.bytec
  implicit val charc:   StringConverter[Char]    = purecsv.safe.converter.defaults.string.charc
  implicit val doublec: StringConverter[Double]  = purecsv.safe.converter.defaults.string.doublec
  implicit val floatc:  StringConverter[Float]   = purecsv.safe.converter.defaults.string.floatc
  implicit val intc:    StringConverter[Int]     = purecsv.safe.converter.defaults.string.intc
  implicit val longc:   StringConverter[Long]    = purecsv.safe.converter.defaults.string.longc
  implicit val shortc:  StringConverter[Short]   = purecsv.safe.converter.defaults.string.shortc
  implicit val stringc: StringConverter[String]  = purecsv.safe.converter.defaults.string.stringc
  implicit def optionc[A](implicit ac: StringConverter[A]): StringConverter[Option[A]] = purecsv.safe.converter.defaults.string.optionc


  // Raw Fields
  implicit val deriveHNil: RawFieldsConverter[HNil] = purecsv.safe.converter.defaults.rawfields.deriveHNil
  implicit def deriveHCons[V, T <: HList](implicit sc:  StringConverter[V],
                                                  fto: RawFieldsConverter[T])
                                                     : RawFieldsConverter[V :: T] = {
    purecsv.safe.converter.defaults.rawfields.deriveHCons
  }
  implicit def deriveClass[A, R](implicit gen: Generic.Aux[A, R],
                                         conv: RawFieldsConverter[R])
                                             : RawFieldsConverter[A] = {
    purecsv.safe.converter.defaults.rawfields.deriveClass
  }

  implicit class CSVRecord[A](a: A)(implicit rfc: RawFieldsConverter[A])
    extends purecsv.csviterable.CSVRecord[A, RawFieldsConverter[A]](a)(rfc)

  implicit class CSVIterable[A](iter: Iterable[A])(implicit rfc: RawFieldsConverter[A])
    extends purecsv.csviterable.CSVIterable[A, RawFieldsConverter[A]](iter)(rfc)

  trait CSVReader[A] extends Serializable {

    def rfc: RawFieldsConverter[A]

    def readCSVFromReader(r: Reader,
                          delimiter:Char = RecordSplitter.defaultFieldSeparator,
                          skipHeader: Boolean = false
                          ): Iterator[Try[A]] = {
      val records = if (skipHeader) {
        RecordSplitterImpl.getRecordsSkipHeader(r, delimiter)
      } else {
        RecordSplitterImpl.getRecords(r, delimiter)
      }
      records.map(record => rfc.tryFrom(record.toSeq))
    }

    def readCSVFromString(s: String,
                          delimiter:Char = RecordSplitter.defaultFieldSeparator,
                          skipHeader: Boolean = false
                          ): List[Try[A]] = {
      val r = new StringReader(s)
      val rs = readCSVFromReader(r, delimiter, skipHeader).toList
      r.close()
      rs
    }

    def readCSVFromFile(f: File,
                        delimiter:Char = RecordSplitter.defaultFieldSeparator,
                        skipHeader: Boolean = false): List[Try[A]] = {
      val r = new BufferedReader(new FileReader(f))
      val rs = readCSVFromReader(r, delimiter, skipHeader).toList
      r.close()
      rs
    }

    def readCSVFromFileName(fileName: String,
                            delimiter:Char = RecordSplitter.defaultFieldSeparator,
                            skipHeader: Boolean = false): List[Try[A]] = {
      readCSVFromFile(new File(fileName), delimiter, skipHeader )
    }

  }

  object CSVReader {
    def apply[A](implicit rfcImp: RawFieldsConverter[A]): CSVReader[A] = new CSVReader[A] {
      override def rfc = rfcImp
    }
  }
}
