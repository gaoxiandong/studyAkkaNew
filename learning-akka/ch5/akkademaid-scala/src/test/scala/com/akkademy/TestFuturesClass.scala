package com.akkademy

import akka.actor.{ActorSystem, Actor}

import scala.concurrent.{Await, Future, Promise}
import scala.util.Try

import scala.concurrent.duration._

/**
  * Created by gaoxd on 2018/7/18.
  */
object TestFuturesClass {
    def main(args: Array[String]) {
//      import scala.concurrent.ExecutionContext.Implicits.global
      val system = ActorSystem()
      implicit val dispatcher = system.dispatchers.lookup("my-dispatcher")
//      println(ArticleParser.apply(TestHelper.file1))

      val futures = (1 to 2000).map(x => {
        Future(ArticleParser.apply(TestHelper.file))
      })

      TestHelper.profile(() =>
        Await.ready(Future.sequence(futures), 30 seconds), "Futures")

      system.shutdown()
    }

}
