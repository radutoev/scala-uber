package org.tzotopia.suber

import akka.actor.typed.scaladsl.{ AbstractBehavior, ActorContext, Behaviors }
import akka.actor.typed.{ ActorRef, Behavior }
import org.tzotopia.suber.Device.{ DeviceId, DeviceMessage, HelloDevice, RespondHello }

object Device {
  type RequestId = Long
  type DeviceId  = String

  sealed trait DeviceMessage
  final case class HelloDevice(requestId: RequestId, replyTo: ActorRef[RespondHello]) extends DeviceMessage

  final case class RespondHello(requestId: RequestId, value: String)

  def apply(deviceId: DeviceId): Behavior[DeviceMessage] =
    Behaviors.setup(context => new Device(context, deviceId))
}

class Device(context: ActorContext[DeviceMessage], deviceId: DeviceId)
    extends AbstractBehavior[DeviceMessage] {
  override def onMessage(msg: DeviceMessage): Behavior[DeviceMessage] = msg match {
    case HelloDevice(requestId, replyTo) =>
      replyTo ! RespondHello(requestId, "hello")
      this
  }
}
