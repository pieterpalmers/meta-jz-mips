include recipes-kernel/linux/linux-jz.inc

DEPENDS += "openssl"

LINUX_VERSION ?= "4.16"
PR = "r6"
#LINUX_VERSION_EXTENSION_append = "-rc7"
LINUX_VERSION_EXTENSION_append = ""
#SRCREV_machine="0c5b9b5d9adbad4b60491f9ba0d2af38904bb4b9"FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${LINUX_VERSION}:"

KBRANCH = "v4.16"
#SRCREV = "7d05020f7ea88483dde607796a072b97611c37f5"
SRCBRANCH = "v4.16"
SRCREV = "${AUTOREV}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${LINUX_VERSION}:"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
FILESEXTRAPATHS_prepend := "files:"

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/torvalds/linux.git;protocol=git"
SRC_URI += "file://defconfig"
SRC_URI += "file://usbserial.cfg"
#SRC_URI += "file:///tmp/yoctopoky/poky/meta-jz-mips/recipes-kernel/linux/linux-creator-ci20-${LINUX_VERSION}/defconfig"
#SRC_URI += "file://no_i40e.cfg"
#SRC_URI += "file://0001-cert-makefile.patch"
#SRC_URI += "file://0002-cpu-probe.patch"

#PV="${LINUX_VERSION}+git${SRCPV}"

COMPATIBLE_MACHINE = "(creator-ci20)"

module_autoload_usbserial = "usbserial"
KERNEL_MODULE_AUTOLOAD += "usbserial"
module_autoload_pl2303 = "pl2303"
KERNEL_MODULE_AUTOLOAD += "pl2303"
#module_autoload_usbserial = "kernel-module-usbserial"
#KERNEL_MODULE_AUTOLOAD += "kernel-module-usbserial"
#module_autoload_pl2303 = "kernel-module-pl2303"
#KERNEL_MODULE_AUTOLOAD += "kernel-module-pl2303"
