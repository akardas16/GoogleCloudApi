package com.akardas16.googlecloud.enums

enum class DeviceType: DeviceTypeInterface {

    SMARTWATCH{
        override fun getDeviceProfile(): String  = "wearable-class-device"
    },
    SMARTPHONE{
        override fun getDeviceProfile(): String = "handset-class-device"
    },
    HEADPHONES{
        override fun getDeviceProfile(): String = "headphone-class-device"
    },
    HOMESPEAKER{
        override fun getDeviceProfile(): String = "small-bluetooth-speaker-class-device"
    },
    HOMESPEAKERSMART{
        override fun getDeviceProfile(): String = "medium-bluetooth-speaker-class-device"
    },
    SMARTTV{
        override fun getDeviceProfile(): String = "large-home-entertainment-class-device"
    },
    CARSPEAKER{
        override fun getDeviceProfile(): String = "large-automotive-class-device"
    },
    IVR{//Interactive Voice Response (IVR) system
    override fun getDeviceProfile(): String = "telephony-class-application"
    },
    /*    DEFAULT{
            override fun getDeviceProfile(): String? = null
        }*/
}

interface DeviceTypeInterface {
    fun getDeviceProfile(): String
}