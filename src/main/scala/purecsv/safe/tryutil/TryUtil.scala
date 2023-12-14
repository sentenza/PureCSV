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

import scala.collection.immutable
import scala.util.{Failure, Success, Try}

package object tryutil {

  implicit class IterableOfTry[A](iter: Iterable[Try[A]]) {

    /** A tuple composed by the successes and the failures */
    lazy val getSuccessesAndFailures: (immutable.List[(Int, A)], immutable.List[(Int, Throwable)]) = {
      val successes = scala.collection.mutable.Buffer[(Int, A)]()
      val failures  = scala.collection.mutable.Buffer[(Int, Throwable)]()
      iter.zipWithIndex.foreach {
        case (Success(a), i) => successes += (i + 1 -> a)
        case (Failure(f), i) => failures += (i + 1  -> f)
      }
      (successes.toList, failures.toList)
    }
  }

  implicit class IteratorOfTry[A](iter: Iterator[Try[A]]) extends IterableOfTry[A](iter.iterator.to(Iterable))

}
