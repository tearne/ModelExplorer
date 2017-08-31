package data

import scala.util.Random

object PretendModel {
  val loadOfRuns: Seq[Run] = (1 to 100).map(_ => doRun)

  def doRun(): Run = Run(
    Map(
      "beta" -> Number(Random.nextDouble(), true),
      "gamma" -> Number(Random.nextDouble(), true),
      "outbreak-size" -> Number(Random.nextInt(10000), false),
      "num-infected" -> Series(Seq(2008,2009,2010), Seq(Random.nextInt(1000),Random.nextInt(1000),Random.nextInt(1000)), false)
    )
  )
}
