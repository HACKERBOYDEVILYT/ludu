package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.rsludo.game.LudoEngine
import com.example.rsludo.model.PlayerColor
import com.example.rsludo.model.Token
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("RS Ludo", appName)
  }

  @Test
  fun `ludo rules verify token base release on 6`() {
    val baseToken = Token(id = 0, playerId = "p1", color = PlayerColor.RED, step = -1)
    assertFalse(LudoEngine.canMove(baseToken, 4))
    assertTrue(LudoEngine.canMove(baseToken, 6))
    assertEquals(0, LudoEngine.calculateNextStep(baseToken, 6))
  }

  @Test
  fun `ludo rules verify token movement and home arrival`() {
    val trackToken = Token(id = 0, playerId = "p1", color = PlayerColor.RED, step = 50)
    assertTrue(LudoEngine.canMove(trackToken, 4))
    assertEquals(54, LudoEngine.calculateNextStep(trackToken, 4))

    // Reaching home target exactly
    val corridorToken = Token(id = 0, playerId = "p1", color = PlayerColor.RED, step = 53)
    assertTrue(LudoEngine.canMove(corridorToken, 3))
    assertEquals(56, LudoEngine.calculateNextStep(corridorToken, 3))

    // Overshooting home target is not allowed
    assertFalse(LudoEngine.canMove(corridorToken, 5))
  }
}
