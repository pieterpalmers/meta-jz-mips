require recipes-bsp/u-boot/u-boot.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

# No patches for other machines yet
COMPATIBLE_MACHINE = "(phoenix|x1000)"

SECURITY_LDFLAGS = "-z,relro -z,now"

BRANCH = "ingenic-linux-kernel3.10.14-x1000-v9.0-20191212"
SCMVERSION ?= "y"
SRCREV = "${AUTOREV}"

SRC_URI = "git:///${TOPDIR}/../ml-git/u-boot-ingenic;protocol=file;branch=${BRANCH}"

UBOOT_VERSION = "2018.10"

PV = "${UBOOT_VERSION}+git${SRCPV}"

# avoid QA issues due to decreasing package versions
inherit gitpkgv
PKGV = "${UBOOT_VERSION}+gitr${GITPKGV}"

LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://README;beginline=2;endline=5;md5=a274fb029fabf05ebeb2bb92905ebe26"

S = "${WORKDIR}/git"
# NOTE: build in source dir to accomodate Ingenic changes
B = "${WORKDIR}/git"

TARGET_LDFLAGS=""

PARALLEL_MAKE=""
