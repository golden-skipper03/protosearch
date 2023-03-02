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
import scodec.bits.BitVector

private[protosearch] final class ArrayIntCodec(codec: Codec[Int], limit: Option[Int] = None)
    extends Codec[Array[Int]] {

  def sizeBound: SizeBound = limit match {
    case None => SizeBound.unknown
    case Some(lim) => codec.sizeBound * lim.toLong
  }

  def encode(array: Array[Int]) =
    codec.encodeAll(array)

  def decode(buffer: BitVector) = codec.collect[Array, Int](buffer, limit)

  override def toString = s"arrayInt($codec)"
}

private[protosearch] final class PostingsCodec(codec: Codec[Int], limit: Option[Int] = None)
    extends Codec[Array[Array[Int]]] {

  def sizeBound: SizeBound = limit match {
    case None => SizeBound.unknown
    case Some(lim) => codec.sizeBound * lim.toLong
  }

  def encode(array2: Array[Array[Int]]) =
    (new ArrayIntCodec(codec, limit)).encodeAll(array2)

  def decode(buffer: BitVector) =
    (new ArrayIntCodec(codec, limit)).collect[Array, Array[Int]](buffer, limit)

  override def toString = s"postings($codec)"
}
