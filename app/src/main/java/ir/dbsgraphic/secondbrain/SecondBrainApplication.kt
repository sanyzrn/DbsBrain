package ir.dbsgraphic.secondbrain

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point. Hilt's object graph is rooted here.
 *
 * Nothing here reaches the network — the system is offline-first by
 * construction (Constitution §10). The encrypted database key is created
 * lazily on first DB access, never at startup, so launch stays instant
 * (Constitution §7).
 */
@HiltAndroidApp
class SecondBrainApplication : Application()
