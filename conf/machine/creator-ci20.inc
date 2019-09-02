#@TYPE: Machine
#@NAME: CI20
#@SOC: Ingenic JZ4780 MIPS
#@DESCRIPTION: MIPS Creator CI20
#@MAINTAINER: Leon Anavi <leon@anavi.org>

require conf/machine/include/tune-mips32r2.inc
IMAGE_CLASSES += "image_types_ci20"

DEFAULTTUNE = "mips32r2el"
MACHINE_FEATURES = "serial"
SERIAL_CONSOLE="115200 ttyS4"

EXTRA_IMAGEDEPENDS += "u-boot-ci20"

KERNEL_IMAGETYPE = "uImage"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-creator-ci20"
PREFERRED_VERSION_linux-creator-ci20 ?= "3.18%"

#PREFERRED_PROVIDER_virtual/kernel ?= "linux-xburst"

PREFERRED_PROVIDER_u-boot = "u-boot-ci20"
PREFERRED_PROVIDER_u-boot-fw-utils = "u-boot-fw-utils-ci20"

#PREFERRED_VERSION_u-boot-ci20 ?= "ci20-v2013.10%"

MACHINE_ESSENTIAL_EXTRA_RDEPENDS = "kernel kernel-modules"

SDCARD_ROOTFS_TYPE ?= "ext3"
SDCARD_ROOTFS ?= "${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${SDCARD_ROOTFS_TYPE}"

IMAGE_FSTYPES ?= "tar.bz2 ext3 sdcard"

MACHINE_FEATURES += " wifi bluetooth"

# For mender to work we have to install the kernel & devicetree.
#IMAGE_INSTALL_append=" kernel kernel-devicetree"

# The mender post-install scripts can install u-boot to do an upgrade
IMAGE_INSTALL_append=" u-boot-ci20 "

# Machine configuration common to all MuuseLabs hardware releases.

# TODO(wdm) Review this. Why are they all needed? And why the double __ ?
PREFERRED_PROVIDER_wpa-supplicant-cli__${MACHINE}       = "wpa-supplicant"
PREFERRED_PROVIDER_wpa-supplicant-passphrase_${MACHINE} = "wpa-supplicant"
PREFERRED_PROVIDER_wpa-supplicant_${MACHINE}            = "wpa-supplicant"

MACHINE_FEATURES_remove = "touchscreen"

# TODO(wdm) Not for final production version ML_DEV_ONLY.
# add USB debugging and launch a console on the first USB port exposed
# This is a machine setting, not an image setting.
# Used by bitbake sysvinit-inittab to create /etc/inittab in the rootfs.
SERIAL_CONSOLES_append = "115200;ttyS4 115200;ttyGS0"
IMAGE_INSTALL_append = " ml-usb-debug"

# Generate /etc/build.
# TODO(wdm) Reproducible builds. Fix the recipes so that the layer hashes
#           fully define the build.
INHERIT_append = " image-buildinfo"
# Note(wdm) Don't include any non-reproducible variables (like DATETIME).
IMAGE_BUILDINFO_VARS = "DISTRO DISTRO_VERSION MACHINE"

# Try to turn off QEUMU support? qemu required to support GObject introspection data.
# TODO(wdm) Needed for gstreamer?
# See https://lists.yoctoproject.org/pipermail/yocto/2016-April/029579.html
MACHINE_FEATURES_BACKFILL_CONSIDERED = "qemu-usermode"
# Try to speed up the build a little?
DISTRO_FEATURES_BACKFILL_CONSIDERED = "gobject-introspection-data"

#disable Mender
BBMASK_append = " meta-mender/meta-mender-core/recipes-bsp/u-boot"
