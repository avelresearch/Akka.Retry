package com.utils

class BaseEmailClient extends EmailClient {

  override def sendEmail(message: String): Unit = {
      throw new Error("Email sending failed...")
  }

}
