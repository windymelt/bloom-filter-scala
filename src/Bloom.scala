package dev.capslock.bloom

import scala.util.hashing.MurmurHash3 as H
import scala.collection.BitSet

private type BitLength  = Int
opaque type BloomFilter = (BitSet, List[String => Int], BitLength)

object BloomFilter {
  def apply(hashes: List[String => Int], bitLength: Int): BloomFilter =
    val bs = BitSet.empty
    (bs, hashes, bitLength)

  def forHashes(nOfHashes: Int, bitLength: Int): BloomFilter =
    val bs = BitSet.empty
    val hs = hashes(nOfHashes, bitLength)
    (bs, hs, bitLength)

  def apply(bits: Int, expectedElemsCount: Int = 1000): BloomFilter =
    val nOfHashes = ((0.7 * bits) / expectedElemsCount).toInt
    if nOfHashes == 0 then throw new IllegalArgumentException("Too few bits")
    forHashes(nOfHashes, bits)

  inline transparent def hash(s: String): Int = H.stringHash(s)

  inline def withInversion(inversion: Int, hash: Int): Int =
    H.mix(hash, inversion)

  inline def withLimit(length: Int, hash: Int): Int =
    // Mask hash to desired bit length
    H.finalizeHash(hash, length) & ((1 << length) - 1)

  private def allowedLength(bits: Int): Int =
    (Math.log(bits) / Math.log(2)).toInt

  def hashes(n: Int, bits: Int): List[String => Int] = (0 until n)
    .map: i =>
      (s: String) =>
        withLimit(
          allowedLength(bits),
          withInversion(hash(i.toString), hash(s)),
        ).abs
    .toList
  end hashes

  // TODO: import from serialized array

  extension (bf: BloomFilter) {
    def add(s: String): BloomFilter =
      val (bs, hs, bl) = bf
      val his          = hs.map(h => h(s))
      val hashbs       = bs | his.toSet
      (hashbs, hs, bl)

    def contains(s: String): Boolean =
      val (bs, hs, _) = bf
      val his         = hs.map(h => h(s))
      his.forall(h => bs(h))

    def toArray: Array[Long] = bf._1.toBitMask

    def falsePositiveRate: Double =
      val (bs, hs, bl) = bf
      val n            = bs.size
      val m            = bl
      val k            = hs.size
      val p            = Math.pow(1 - Math.exp(-k * n.toDouble / m), k)
      p
    end falsePositiveRate
  }
}
