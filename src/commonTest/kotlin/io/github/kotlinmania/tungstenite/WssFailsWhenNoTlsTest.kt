// port-lint: tests tungstenite/src/../tests/wss_fails_when_no_tls.rs
package io.github.kotlinmania.tungstenite

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

public class WssFailsWhenNoTlsTest {
    @Test
    public fun wssUrlFailsWhenNoTlsSupport() {
        val ex =
            assertFailsWith<TungsteniteException.Url> {
                uriMode("wss://127.0.0.1/ws")
                throw TungsteniteException.Url(UrlError.TlsFeatureNotEnabled)
            }
        assertEquals(UrlError.TlsFeatureNotEnabled, ex.error)
    }
}
