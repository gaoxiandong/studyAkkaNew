package com.akkademy

import akka.actor.{ActorRef, Props, ActorSystem}
import akka.routing.BalancingPool
import com.akkademy.TestHelper.TestCameoActor

import scala.concurrent.{Await, Promise}
import scala.concurrent.duration._

/**
  * Created by gaoxd on 2018/7/19.
  */
object TestBalanceingPool {

  def main(args: Array[String]) {
    val system = ActorSystem()

    val p = Promise[String]()

    val workerRouter = system.actorOf(BalancingPool(8).props(Props(classOf[ArticleParseActor])),
      "balancing-pool-router")

    val cameoActor: ActorRef =
      system.actorOf(Props(new TestCameoActor(p)))

    (0 to 1999).foreach(x => {
      workerRouter.tell(
        new ParseArticle(TestHelper.file1)
        , cameoActor);
    })

    TestHelper.profile(() => Await.ready(p.future, 20 seconds), "ActorsInBalacingPool")

    system.shutdown()
  }

}
