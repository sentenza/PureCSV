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

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import purecsv.config.Headers
import purecsv.config.Trimming.TrimAll
import purecsv.unsafe._
import purecsv.unsafe.converter.RawFieldsConverter

final case class Person(name: String, surname: String)

class CSVRecordTest extends AnyFunSuite with Matchers {
  val person = Person("Jon", """Snow "III" of Winterfell""")

  test("CSVRecord output should be parsable by purecsv") {
    implicit val rfc = RawFieldsConverter[Person]
    val csvRecord = CSVRecord(person).toCSV()
    CSVReader[Person].readCSVFromString(csvRecord, headers = Headers.None) should contain theSameElementsAs Seq(person)
  }
  test("CSVRecord output should be parsable by purecsv (trimming applied)") {
    implicit val rfc = RawFieldsConverter[Person]
    val csvRecord = """Jon,"Snow ""III"" of Winterfell """"
    CSVReader[Person].readCSVFromString(csvRecord, trimming = TrimAll, headers = Headers.None) should contain theSameElementsAs Seq(person)
  }
}
