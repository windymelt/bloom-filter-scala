import mill._, scalalib._

object app extends RootModule with ScalaModule {
  def scalaVersion = "3.4.1"

  object test extends ScalaTests with TestModule.ScalaTest {
    def ivyDeps = Agg(
      ivy"org.scalactic::scalactic:3.2.19",
      ivy"org.scalatest::scalatest:3.2.19",
      ivy"org.scalacheck::scalacheck:1.15.4",
      ivy"org.scalatestplus::scalacheck-1-18:3.2.19.0",
    )
  }
}
