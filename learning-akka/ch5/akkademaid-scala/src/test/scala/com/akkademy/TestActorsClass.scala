package com.akkademy

import akka.actor.{Props, ActorRef, ActorSystem}
import akka.routing.RoundRobinPool
import com.akkademy.TestHelper.TestCameoActor

import scala.concurrent.{Await, Promise, Future}

import scala.concurrent.duration._
/**
  * Created by gaoxd on 2018/7/19.
  */
object TestActorsClass {
  def main(args: Array[String]) {
    val system = ActorSystem()

    val workerRouter: ActorRef =
      system.actorOf(
        Props.create(classOf[ArticleParseActor]).
          withDispatcher("my-dispatcher").
          withRouter(new RoundRobinPool(8)), "workerRouter")

    println("workerRouter="+workerRouter)
//    val future: Future[Int] = Future{
//      1
//    }(system.dispatcher)


    val promise = Promise[String]()

    val cameoActor: ActorRef =
      system.actorOf(Props(new TestCameoActor(promise)))

    println("cameoActor="+cameoActor)
    (0 to 1999).foreach(x => {
      workerRouter.tell(
        new ParseArticle(TestHelper.file1)
        , cameoActor);
    })

    TestHelper.profile(() => Await.ready(promise.future, 20 seconds), "Actors")


    system.shutdown()
  }
}
