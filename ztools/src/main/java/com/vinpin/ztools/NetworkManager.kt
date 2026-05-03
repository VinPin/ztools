package com.vinpin.ztools

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 网络类型枚举
 */
enum class NetworkType {
    /** 无网络 */
    NONE,

    /** WiFi网络 */
    WIFI,

    /** 移动数据网络 */
    CELLULAR
}

/**
 * 网络状态变化观察者
 */
interface NetworkStateObserver {
    /**
     * 网络状态变化回调
     * @param isConnected 是否连接网络
     * @param type 当前网络类型
     */
    fun onNetworkStateChanged(isConnected: Boolean, type: NetworkType)
}

/**
 * 全局网络状态管理器
 * 提供网络状态判断、类型获取、状态监听功能
 * 自动适配 Android 7.0 前后的 API，线程安全
 */
object NetworkManager {

    /** 全局锁 */
    private val lock = Any()

    /** 初始化标记 */
    @Volatile
    private var isInitialized = false

    /** 网络连接管理器 */
    private lateinit var connectivityManager: ConnectivityManager

    /** 网络状态监听集合（线程安全） */
    private val observers = CopyOnWriteArrayList<NetworkStateObserver>()

    /** Android 7.0+ 网络回调 */
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    /** 低版本网络广播接收者 */
    private var networkReceiver: NetworkReceiver? = null

    /**
     * 初始化网络管理器
     * 必须在 Application 中调用，自动注册网络监听
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun init(context: Context) {
        if (!isInitialized) {
            synchronized(lock) {
                if (!isInitialized) {
                    val appContext = context.applicationContext
                    connectivityManager =
                        appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    registerNetworkCallback(appContext)
                    isInitialized = true
                }
            }
        }
    }

    /**
     * 判断当前是否有可用网络连接
     */
    fun isNetworkConnected(): Boolean {
        return getCurrentNetworkType() != NetworkType.NONE
    }

    /**
     * 获取当前网络类型
     */
    fun getCurrentNetworkType(): NetworkType {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getNetworkTypeApi23()
            } else {
                getNetworkTypeLegacy()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkType.NONE
        }
    }

    /**
     * 添加网络状态变化监听
     *
     * @param observer 网络状态观察者
     */
    fun addNetworkObserver(observer: NetworkStateObserver) {
        observers.addIfAbsent(observer)
        // 立即回调一次当前状态
        UiThreadUtil.runOnUiThread {
            observer.onNetworkStateChanged(isNetworkConnected(), getCurrentNetworkType())
        }
    }

    /**
     * 移除网络状态监听
     *
     * @param observer 网络状态观察者
     */
    fun removeNetworkObserver(observer: NetworkStateObserver) {
        observers.remove(observer)
    }

    /**
     * 移除所有网络监听
     */
    fun removeAllNetworkObserver() {
        observers.clear()
    }

    /**
     * 释放资源，反注册监听，防止内存泄漏
     */
    fun release() {
        if (isInitialized) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    networkCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
                } else {
                    networkReceiver?.let { Util.getApp().unregisterReceiver(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            networkCallback = null
            networkReceiver = null
            removeAllNetworkObserver()
            isInitialized = false
        }
    }

    /**
     * 注册网络监听（适配高低版本）
     */
    private fun registerNetworkCallback(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Android 7.0+ 使用 NetworkCallback
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    dispatchStateChanged()
                }

                override fun onLost(network: Network) {
                    dispatchStateChanged()
                }

                override fun onCapabilitiesChanged(
                    network: Network, capabilities: NetworkCapabilities
                ) {
                    dispatchStateChanged()
                }
            }
            connectivityManager.registerNetworkCallback(request, networkCallback!!)
        } else {
            // 低版本使用广播接收者
            networkReceiver = NetworkReceiver()
            val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            context.registerReceiver(networkReceiver, filter)
        }
    }

    /**
     * 分发网络状态变化（主线程回调）
     */
    private fun dispatchStateChanged() {
        val connected = isNetworkConnected()
        val type = getCurrentNetworkType()
        UiThreadUtil.runOnUiThread {
            observers.forEach {
                runCatching { it.onNetworkStateChanged(connected, type) }
                    .onFailure { e -> e.printStackTrace() }
            }
        }
    }

    /**
     * Android 6.0+ 获取网络类型
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun getNetworkTypeApi23(): NetworkType {
        val network = connectivityManager.activeNetwork ?: return NetworkType.NONE
        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return NetworkType.NONE
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
            else -> NetworkType.NONE
        }
    }

    /**
     * 低版本获取网络类型
     */
    @Suppress("DEPRECATION")
    private fun getNetworkTypeLegacy(): NetworkType {
        val info: NetworkInfo = connectivityManager.activeNetworkInfo ?: return NetworkType.NONE
        if (!info.isConnected) return NetworkType.NONE
        return when (info.type) {
            ConnectivityManager.TYPE_WIFI -> NetworkType.WIFI
            ConnectivityManager.TYPE_MOBILE -> NetworkType.CELLULAR
            else -> NetworkType.NONE
        }
    }

    /**
     * 低版本网络广播接收者
     */
    private class NetworkReceiver : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                dispatchStateChanged()
            }
        }
    }
}