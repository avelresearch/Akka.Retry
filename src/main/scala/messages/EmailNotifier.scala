package messages

object EmailNotifier {

  case class SendEmail(message: String)

  case class RetrySendEmail( email: SendEmail, counter: Int )

}
