include recipes-kernel/linux/linux-jz.inc

LINUX_VERSION ?= "3.18.3"
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${LINUX_VERSION}:"

KBRANCH = "ci20-v3.18"
SRCREV = "7dff33297116643485ca37141d804eddd793e834"

DEPENDS += "lzop-native bc-native"

SRC_URI = "git://github.com/MIPS/CI20_linux.git;branch=${KBRANCH} \
           file://defconfig \
           file://0001-fix-gcc8-compilation-issue.patch \
           file://0002-fix-gcc-compilation-warning.patch \
           "

COMPATIBLE_MACHINE = "(creator-ci20)"
