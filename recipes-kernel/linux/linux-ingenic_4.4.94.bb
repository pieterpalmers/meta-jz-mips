DESCRIPTION = "Kernel"
HOMEPAGE = "http://nohomepage.org"
SECTION = "kernel"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel

LINUX_VERSION ?= "4.4.94"
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${LINUX_VERSION}:"

#KERNEL_SRC = "git://git@github.com/TheoMarescaux/linux-imx-ml;protocol=ssh;branch=${SRCBRANCH}"
KERNEL_SRC = "git:///${TOPDIR}/../ml-git/linux-imx-ml;protocol=file"

PV = "${LINUX_VERSION}+git${SRCPV}"

S = "${WORKDIR}/git"

# Put a local version until we have a true SRCREV to point to
LOCALVERSION = "-ml"
LOCALVERSION_phoenix = "-phoenix"

# avoid QA issues due to decreasing package versions
inherit gitpkgv
PKGV = "0.1+gitr${GITPKGV}"


DEPENDS += "lzop-native bc-native"

SCMVERSION ?= "y"
SRCBRANCH = "v4.4.94-x1000"
#SRCREV = "8d0ff94a255432654228d24c94a7e1b65843e2b4"
SRCREV = "${AUTOREV}"
SRC_URI = "${KERNEL_SRC};branch=${SRCBRANCH} \
"
#           file://${MACHINE}/defconfig 
#           file://${MACHINE}/*.dts* 

COMPATIBLE_MACHINE = "(phoenix)"

