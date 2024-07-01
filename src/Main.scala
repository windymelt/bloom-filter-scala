package dev.capslock.bloom

import scala.util.hashing.MurmurHash3 as H
import scala.collection.BitSet

@main def main(): Unit = {
  val bf  = BloomFilter(4096, 1000)
  val bf2 = bf.add("hello")
  val bf3 = bf2.add("world")
  val bf4 = bf3.add("!")
  val bf5 = bf4.add("hello")

  println(bf5)
  println(bf5.toArray.toVector)
  println(bf5.toArray.size)
  println(bf5.contains("hello"))
  println(bf5.contains("foobar"))
  println(s"False positive rate: ${bf5.falsePositiveRate}")
}
