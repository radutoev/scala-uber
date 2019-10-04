package org.tzotopia.suber

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.WordSpecLike
import org.tzotopia.suber.Device.{ HelloDevice, RespondHello }

class DeviceTest extends ScalaTestWithActorTestKit with WordSpecLike {
  "Device Actor" must {
    "reply with hello when queried" in {
      val probe       = createTestProbe[RespondHello]
      val deviceActor = spawn(Device("testDevice"))

      deviceActor ! HelloDevice(requestId = 42L, probe.ref)
      val response = probe.receiveMessage()
      response.requestId should equal(42L)
      response.value     should equal("hello")
    }
  }
}
