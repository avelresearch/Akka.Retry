package com.actors

import actors.EmailNotifier
import akka.actor.{Actor, ActorRef, Props, SupervisorStrategy}
import akka.event.Logging
import akka.routing.RoundRobinPool
import messages.EmailNotifier.{RetrySendEmail, SendEmail}
import messages.Master.{Check, SendEmailFailed}

import scala.concurrent.duration._

class Master extends Actor {

  val log = Logging(context.system, this)

  val emailHandlerPool: ActorRef = context.actorOf( RoundRobinPool(5, supervisorStrategy = SupervisorStrategy.defaultStrategy)
    .props( Props.create(classOf[EmailNotifier], self) ) )

  import scala.concurrent.ExecutionContext.Implicits.global

  def receive = {

    case SendEmailFailed(email, counter) => {
      log.info(s"Retry email with: $counter")
      context.system.scheduler.scheduleOnce(100 milliseconds) {
        log.info(s"Resend with counter: $counter")
        emailHandlerPool ! RetrySendEmail(SendEmail(email.message), counter + 1)
      }
    }

    case Check => {
      log.info("Send test email")
      emailHandlerPool ! SendEmail("This is a test email")
    }

  }



}
