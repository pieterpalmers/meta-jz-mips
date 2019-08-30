DESCRIPTION = "Kernel"
HOMEPAGE = "http://nohomepage.org"
SECTION = "kernel"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

inherit kernel

LINUX_VERSION ?= "5.3"
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${LINUX_VERSION}:"

PV = "${LINUX_VERSION}+git${SRCPV}"

S = "${WORKDIR}/git"

KBRANCH = "xburst"
SRCREV = "${AUTOREV}"

DEPENDS += "lzop-native bc-native"

SRC_URI = "\
	git://github.com/XBurst/Linux-XBurst.git;branch=${KBRANCH} \
	file://defconfig \
	"

COMPATIBLE_MACHINE = "(phoenix-sd)"
