# Copyright (C) 2020 Muuselabs SA/NV
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Kernel"
HOMEPAGE = "http://nohomepage.org"
SECTION = "kernel"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

inherit kernel
require linux-localconfig.inc

S = "${WORKDIR}/git"

KERNEL_IMAGETYPE = "uImage"

KERNEL_SRC = "git://git@github.com/TheoMarescaux/linux-imx-ml;protocol=ssh;branch=${SRCBRANCH}"
LINUX_VERSION ?= "5.7"
PV = "${LINUX_VERSION}+git${SRCPV}"

# avoid QA issues due to decreasing package versions
inherit gitpkgv
PKGV = "${LINUX_VERSION}+gitr${GITPKGV}"

# Put a local version until we have a true SRCREV to point to
LOCALVERSION = "-ml"
LOCALVERSION_phoenix = "-x1000"

SCMVERSION ?= "y"
SRCBRANCH = "xburst-v5.7-ml"
SRCREV = "${AUTOREV}"

SRC_URI = "${KERNEL_SRC};branch=${SRCBRANCH} \
           file://${MACHINE}/defconfig \
           file://${MACHINE}/*.dts* \
"

COMPATIBLE_MACHINE = "(x1000)"
