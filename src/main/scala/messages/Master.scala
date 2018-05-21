package messages

import messages.EmailNotifier.SendEmail

object Master {

  case object Check
  case class SendEmailFailed( email: SendEmail, counter: Int )
}
