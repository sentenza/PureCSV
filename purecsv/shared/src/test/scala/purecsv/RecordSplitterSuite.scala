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

import java.io.CharArrayReader

import purecsv.unsafe.RecordSplitterImpl
import org.scalatest.{FunSuite, Matchers}


class RecordSplitterSuite extends FunSuite with Matchers {

  test("RecordSplitterImpl works with no records") {
    val reader = new CharArrayReader("".toCharArray)
    RecordSplitterImpl.getRecords(reader).toSeq should contain theSameElementsInOrderAs(Seq.empty)
  }

  test("RecordSplitterImpl works with two records") {
    val reader = new CharArrayReader("foo,bar\nbar,foo".toCharArray)
    RecordSplitterImpl.getRecords(reader).toSeq should contain theSameElementsInOrderAs(Seq(Array("foo","bar"),Array("bar","foo")))
  }


  test("RecordSplitterImpl works with custom delimiter") {
    val reader = new CharArrayReader("foo|bar\nbar|foo".toCharArray)
    RecordSplitterImpl.getRecords(reader, '|').toSeq should contain theSameElementsInOrderAs(Seq(Array("foo","bar"),Array("bar","foo")))
  }

  test("RecordSplitterImpl works with custom UTF8 delimiter") {
    val reader = new CharArrayReader("foo☃bar\nbar☃foo".toCharArray)
    RecordSplitterImpl.getRecords(reader, '☃').toSeq should contain theSameElementsInOrderAs(Seq(Array("foo","bar"),Array("bar","foo")))
  }
}