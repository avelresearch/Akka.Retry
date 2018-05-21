import akka.actor.{ActorSystem, Props}
import com.actors.Master
import messages.Master.Check

import scala.concurrent.duration._
import scala.concurrent.ExecutionContextExecutor

object MainRunner extends App {

  implicit val system: ActorSystem = ActorSystem("RetryTesting")
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val master = system.actorOf(Props[Master])

  system.scheduler.schedule(5 seconds, 10 seconds, master, Check)


}
