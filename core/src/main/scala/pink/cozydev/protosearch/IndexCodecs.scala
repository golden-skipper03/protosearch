/*
 * Copyright 2022 CozyDev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pink.cozydev.protosearch

import scodec._

object IndexCodecs {
  def termListOfN(countCodec: Codec[Int], strCodec: Codec[String]): Codec[Array[String]] =
    countCodec
      .flatZip(count => new TermListCodec(strCodec, Some(count)))
      .narrow(
        { case (cnt, xs) =>
          if (xs.size == cnt) Attempt.successful(xs)
          else {
            val valueBits = strCodec.sizeBound.exact.getOrElse(strCodec.sizeBound.lowerBound)
            Attempt.failure(Err.insufficientBits(cnt * valueBits, xs.size * valueBits))
          }
        },
        (xs: Array[String]) => (xs.size, xs),
      )
      .withToString(s"termListOfN($countCodec, $strCodec)")

  def postingsOfN(countCodec: Codec[Int], intCodec: Codec[Int]): Codec[Array[Array[Int]]] =
    countCodec
      .flatZip(count => new PostingsCodec(intCodec, Some(count)))
      .narrow(
        { case (cnt, xs) =>
          if (xs.size == cnt) Attempt.successful(xs)
          else {
            val valueBits = intCodec.sizeBound.exact.getOrElse(intCodec.sizeBound.lowerBound)
            Attempt.failure(Err.insufficientBits(cnt * valueBits, xs.size * valueBits))
          }
        },
        (xs: Array[Array[Int]]) => (xs.size, xs),
      )
      .withToString(s"postingsOfN($countCodec, $intCodec)")

  val termList = termListOfN(codecs.vint, codecs.utf8_32)
  val postings = postingsOfN(codecs.vint, codecs.vint)
}
