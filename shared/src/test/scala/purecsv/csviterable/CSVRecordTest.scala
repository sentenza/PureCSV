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
package purecsv.csviterable

import org.scalatest.{ FunSuite, Matchers }
import purecsv.unsafe._
import purecsv.unsafe.converter.RawFieldsConverter

final case class Person(name: String, surname: String)

class CSVRecordTest extends FunSuite with Matchers {

  test("CSVRecord output should be parsable by purecsv") {
    val person = Person("Jon", "Snow \"III\" of Winterfell")
    implicit val rfc = RawFieldsConverter[Person]
    val csvRecord = CSVRecord(person).toCSV()
    println(s"csvRecord: $csvRecord")
    CSVReader[Person].readCSVFromString(csvRecord) should contain theSameElementsAs Seq(person)
  }
}
