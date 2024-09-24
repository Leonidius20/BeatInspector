package ua.leonidius.beatinspector.infrastructure.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class Connectivity @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val conManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

    val isConnected: Flow<Boolean> = callbackFlow {

        // initial value
        val activeNetworkInfo = conManager.getActiveNetworkInfo()
        trySend(activeNetworkInfo != null && activeNetworkInfo.isConnected)


        val callback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            conManager.registerDefaultNetworkCallback(callback)
        } else {
            conManager.registerNetworkCallback(
                NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build(),
                callback
            )
        }

        awaitClose { conManager.unregisterNetworkCallback(callback) }

    }

}