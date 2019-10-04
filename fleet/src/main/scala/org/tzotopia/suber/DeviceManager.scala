package org.tzotopia.suber

import akka.actor.typed.scaladsl.{ AbstractBehavior, ActorContext, Behaviors }
import akka.actor.typed.{ ActorRef, Behavior }
import org.tzotopia.suber.Device.{ DeviceId, DeviceMessage }
import org.tzotopia.suber.DeviceManager.{ DeviceManagerMessage, DeviceRegistered, RegisterDevice }

object DeviceManager {
  sealed trait DeviceManagerMessage
  final case class RegisterDevice(deviceId: DeviceId, replyTo: ActorRef[DeviceRegistered])
      extends DeviceManagerMessage

  final case class DeviceRegistered(device: ActorRef[DeviceMessage])

  def apply(): Behavior[DeviceManagerMessage] =
    Behaviors.setup(context => new DeviceManager(context))
}

class DeviceManager(context: ActorContext[DeviceManagerMessage])
    extends AbstractBehavior[DeviceManagerMessage] {
  private var deviceIdToActor = Map.empty[DeviceId, ActorRef[DeviceMessage]]

  override def onMessage(msg: DeviceManagerMessage): Behavior[DeviceManagerMessage] = msg match {
    case RegisterDevice(deviceId, replyTo) =>
      deviceIdToActor.get(deviceId) match {
        case Some(device) =>
          replyTo ! DeviceRegistered(device)
        case None =>
          val deviceActor = context.spawn(Device(deviceId), s"device-$deviceId")
          deviceIdToActor += deviceId -> deviceActor
          replyTo ! DeviceRegistered(deviceActor)
      }
      this
  }
}
