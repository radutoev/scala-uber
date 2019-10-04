package org.tzotopia.suber

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.WordSpecLike
import org.tzotopia.suber.DeviceManager.{ DeviceRegistered, RegisterDevice }

class DeviceManagerTest extends ScalaTestWithActorTestKit with WordSpecLike {
  "A DeviceManager" should {
    "return same actor for same deviceId" in {
      val probe      = createTestProbe[DeviceRegistered]()
      val groupActor = spawn(DeviceManager())

      groupActor ! RegisterDevice("device1", probe.ref)
      val registered1 = probe.receiveMessage()

      // registering same again should be idempotent
      groupActor ! RegisterDevice("device1", probe.ref)
      val registered2 = probe.receiveMessage()

      registered1.device should equal(registered2.device)
    }
  }
}
