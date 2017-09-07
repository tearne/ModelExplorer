package data

import play.api.libs.json.Json

import scala.util.Random

object PretendModel {
  val loadOfRuns: Seq[Run] = (1 to 100).map(_ => doRun)

  def doRun(): Run = Run(
    Meta(Seq("beta", "gamma"), Seq("outbreak-size", "culls")),
    Map(
      "beta" -> Number(Random.nextDouble()),
      "gamma" -> Number(Random.nextDouble()),
      "outbreak-size" -> Number(Random.nextInt(10000)),
      "culls" -> Series(Seq(2008,2009,2010), Seq(Random.nextInt(1000),Random.nextInt(1000),Random.nextInt(1000)))
    )
  )
}
