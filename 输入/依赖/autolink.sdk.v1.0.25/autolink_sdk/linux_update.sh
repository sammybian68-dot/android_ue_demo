adb wait-for-device
adb root
adb wait-for-device
adb remount
adb wait-for-device
adb push etc/autolink.sdk.xml /system/etc/permissions/autolink.sdk.xml
adb push arm64/boot-autolink.sdk.art /system/framework/arm64/boot-autolink.sdk.art
adb push arm64/boot-autolink.sdk.oat /system/framework/arm64/boot-autolink.sdk.oat
adb push arm64/boot-autolink.sdk.vdex /system/framework/arm64/boot-autolink.sdk.vdex
adb push arm/boot-autolink.sdk.art /system/framework/arm/boot-autolink.sdk.art
adb push arm/boot-autolink.sdk.oat /system/framework/arm/boot-autolink.sdk.oat
adb push arm/boot-autolink.sdk.vdex /system/framework/arm/boot-autolink.sdk.vdex
adb push autolink.sdk.jar /system/framework/autolink.sdk.jar
adb push boot-autolink.sdk.vdex /system/framework/boot-autolink.sdk.vdex
