package actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.event.Logging
import com.utils.BaseEmailClient
import messages.EmailNotifier.{RetrySendEmail, SendEmail}
import messages.Master.SendEmailFailed

import scala.util.{Failure, Success, Try}

class EmailNotifier(master: ActorRef) extends Actor with ActorLogging {

  val maxNumberOfRetries = 5

  val logger = Logging(context.system, this)

  val client = new BaseEmailClient()

  def receive = {

    case SendEmail(content) =>
      self ! RetrySendEmail( SendEmail(content), 0 )

    case RetrySendEmail(email, counter) =>
      {

        log.info("EmailNotifier: try send email")

        val result = Try{
          client.sendEmail(email.message)
        } match {
          case Success(_) => Right("Good")
          case Failure(ex) => Left(ex.getMessage)
        }

        result match {
          case Right(_) => log.info("Passed")
          case Left(_) if (counter < maxNumberOfRetries - 1) => {
            log.error(s"Failed to send email. $counter")
            master ! SendEmailFailed( email, counter)
          }
          case Left(ex) =>
            log.error(s"EmailNotifier: failed to send email after: $counter")
        }
      }


  }
}
