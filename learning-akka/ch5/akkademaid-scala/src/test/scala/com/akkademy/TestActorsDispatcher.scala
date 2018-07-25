package com.akkademy

import akka.actor.{Props, ActorRef, ActorSystem}
import akka.routing.RoundRobinGroup
import com.akkademy.TestHelper.TestCameoActor

import scala.concurrent.{Await, Promise}
import scala.concurrent.duration._

/**
  * Created by gaoxd on 2018/7/19.
  */
object TestActorsDispatcher {

  def main(args: Array[String]) {
    val system = ActorSystem()

    val p = Promise[String]()

    val actors: IndexedSeq[ActorRef] = (0 to 7).map(x => {
      system.actorOf(Props(classOf[ArticleParseActor]).
        withDispatcher("article-parsing-dispatcher"))
    })

    val workerRouter = system.actorOf(RoundRobinGroup(
      actors.map(x => x.path.toStringWithoutAddress).toList).props(),
      "workerRouter")

    val cameoActor: ActorRef =
      system.actorOf(Props(new TestCameoActor(p)))

    (0 to 1999).foreach(x => {
      workerRouter.tell(
        new ParseArticle(TestHelper.file1)
        , cameoActor);
    })

    TestHelper.profile(() => Await.ready(p.future, 20 seconds), "ActorsAssignedToDispatcher")

    system.shutdown()
  }

}
