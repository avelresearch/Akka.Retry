package actors

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import com.utils.BaseEmailClient
import messages.EmailNotifier.SendEmail
import messages.Master.SendEmailFailed

import scala.util.{Failure, Success, Try}

class EmailNotifier(master: ActorRef) extends Actor {

  val maxNumberOfRetries = 5
  val log = Logging(context.system, this)

  val client = new BaseEmailClient()

  def receive = {

    case SendEmail(content) =>
      self ! SendEmailFailed( SendEmail(content), 0 )

    case SendEmailFailed(email, counter) =>
      {
        val result = Try{
          client.sendEmail(email.message)
        } match {
          case Success(_) => Right("Good")
          case Failure(ex) => Left(ex.getMessage)
        }

        result match {
          case Right(_) => log.info("Passed")
          case Left(_) if (counter < maxNumberOfRetries) => {
            log.error(s"Failed to send email. $counter")
            master ! SendEmailFailed( email, counter)
          }
        }
      }


  }
}
