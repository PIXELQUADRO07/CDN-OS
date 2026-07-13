# CDNOS Tegu (Pixel 9a) Customizations

PRODUCT_BRAND := CDNOS
PRODUCT_MODEL := CDNOS Pixel 9a
PRODUCT_MANUFACTURER := Google

PRODUCT_PRODUCT_PROPERTIES += \
    ro.product.brand="CDN OS" \
    ro.product.model="CDN OS Pixel 9a" \
    ro.build.display.id="CDN OS 1.0" \
    dalvik.vm.heapsize=512m \
    dalvik.vm.heapstartsize=16m \
    dalvik.vm.heapgrowthlimit=256m

# ==========================================
# Custom Boot Animation
# ==========================================
PRODUCT_COPY_FILES += \
    device/google/tegu/bootanimation/bootanimation.zip:$(TARGET_COPY_OUT_PRODUCT)/media/bootanimation.zip

# ==========================================
# CDNOS System Apps
# ==========================================
PRODUCT_PACKAGES += \
    CDNSetupWizard \
    privapp-permissions-CDNSetupWizard \
    AuroraStore \
    FDroid \
    Magisk \
    CDNPrivacyCenter \
    privapp-permissions-CDNPrivacyCenter

# ==========================================
# Setup Wizard Configuration
# ==========================================
# Disable AOSP Provision app
PRODUCT_PACKAGES_OVERRIDE += \
    Provision

# Force custom Setup Wizard
PRODUCT_SYSTEM_EXT_PROPERTIES += \
    ro.setupwizard.mode=REQUIRED

# ==========================================
# SELinux Policy Integration
# ==========================================
BOARD_VENDOR_SEPOLICY_DIRS += device/google/tegu-sepolicy
