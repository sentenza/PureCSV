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
package purecsv.safe

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.util.{Failure, Success}

class MyException(val s: String) extends RuntimeException(s) {
  override def equals(o: Any): Boolean = o match {
    case e:MyException => s.equals(e.s)
    case _ => false
  }
}

class TryUtilSuite extends AnyFunSuite with Matchers {
  import tryutil._

  def failure(s: String): Failure[RuntimeException] = Failure(new MyException(s))

  test("getSuccessesAndFailures partition an Iterator[Try[A]] into successes and failures") {
    val startingSuccesses = Seq(Success(1),Success(2))
    val startingFailures = Seq(failure("3"),failure("4"))
    val expectedSuccesses = Seq(1 -> 1, 2 -> 2)
    val expectedFailures = Seq(3 -> new MyException("3"), 4 -> new MyException("4"))
    val (resSuccesses,resFailures) = (startingSuccesses ++ startingFailures).getSuccessesAndFailures
    resSuccesses should be (expectedSuccesses)
    resFailures should be (expectedFailures)
  }
}
